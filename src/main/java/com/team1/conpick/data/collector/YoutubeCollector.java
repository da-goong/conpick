package com.team1.conpick.data.collector;

import com.team1.conpick.data.comment.Comment;
import com.team1.conpick.data.comment.CommentRepository;
import com.team1.conpick.data.video.Video;
import com.team1.conpick.data.video.VideoStats;
import com.team1.conpick.data.channel.ChannelStats;
import com.team1.conpick.data.channel.ChannelStatsRepository;
import com.team1.conpick.data.video.VideoRepository;
import com.team1.conpick.data.video.VideoStatsRepository;
import com.team1.conpick.global.exception.YoutubeChannelNotFoundException;
import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeGoogleClient;
import com.team1.conpick.youtube.YoutubeService;
import com.team1.conpick.youtube.dto.YoutubeChannelResponse;
import com.team1.conpick.youtube.dto.YoutubeCommentResponse;
import com.team1.conpick.youtube.dto.YoutubePlaylistItemResponse;
import com.team1.conpick.youtube.dto.YoutubeVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeCollector {

    private final YoutubeService youtubeService;
    private final YoutubeGoogleClient youtubeGoogleClient;
    private final ChannelStatsRepository channelStatsRepository;
    private final VideoRepository videoRepository;
    private final VideoStatsRepository videoStatsRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void collect(YoutubeAccount youtubeAccount){

        String accessToken = youtubeService.getValidAccessToken(youtubeAccount);
        YoutubeChannelResponse response = youtubeGoogleClient.fetchMyChannel(accessToken);

        if(response.items() == null || response.items().isEmpty()){
            throw new YoutubeChannelNotFoundException("channel을 찾지 못했습니다.");
        }

        // subscriberCount, videoCount, viewCount
        YoutubeChannelResponse.Item channelItem = response.items().get(0);
        YoutubeChannelResponse.Statistics stats = channelItem.statistics();
        String subscriberCountStr = stats.subscriberCount();
        String videoCountStr = stats.videoCount();
        String viewCountStr = stats.viewCount();

        Long subscriberCount = toLong(subscriberCountStr);
        Long videoCount = toLong(videoCountStr);
        Long viewCount = toLong(viewCountStr);

        ChannelStats channelStats= ChannelStats.builder()
                .youtubeAccount(youtubeAccount)
                .subscriberCount(subscriberCount)
                .videoCount(videoCount)
                .viewCount(viewCount)
                .build();

        channelStatsRepository.save(channelStats);

        // uploads

        String uploads = channelItem.contentDetails().relatedPlaylists().uploads();
        log.info("uploads playlist = {}", uploads);

        YoutubePlaylistItemResponse playlistItemResponse = youtubeGoogleClient.fetchPlaylistItems(accessToken, uploads);

        List<YoutubePlaylistItemResponse.Item> playlistItems = playlistItemResponse.items();
        if(playlistItems == null){
            playlistItems = List.of();
        }

        List<String> videoIds = new ArrayList<>();
        for (YoutubePlaylistItemResponse.Item item : playlistItems) {
            videoIds.add(item.contentDetails().videoId());
        }

        log.info("video ids ({}) = {}", videoIds.size(), videoIds);


        // 2. fetchVideos 호출 → 결과를 변수에 담기
       YoutubeVideoResponse youtubeVideoResponse = youtubeGoogleClient.fetchVideos(accessToken, videoIds);

        // 3. items null 처리 (2-2와 같은 방식)
        List<YoutubeVideoResponse.Item> videoItems = youtubeVideoResponse.items();
        if(videoItems == null){
            videoItems = List.of();
        }

        // 4. for문으로 영상마다 로그:
        for (YoutubeVideoResponse.Item videoItem : videoItems) {
            log.info("video id={}, title={}, views={}, publishedAt={}", videoItem.id(), videoItem.snippet().title(), toLong(videoItem.statistics().viewCount()) ,videoItem.snippet().publishedAt());
            Video video = saveVideo(youtubeAccount, videoItem);
            saveComments(accessToken, video);
        }




    }

    private Video saveVideo(YoutubeAccount youtubeAccount, YoutubeVideoResponse.Item videoItem){
        YoutubeVideoResponse.Snippet snippet= videoItem.snippet();
        YoutubeVideoResponse.Statistics statistics = videoItem.statistics();

        String thumbnailUrl = null;
        if(snippet.thumbnails() != null && snippet.thumbnails().medium() != null){
            thumbnailUrl = snippet.thumbnails().medium().url();
        }

        Optional<Video> found = videoRepository.findByYoutubeVideoId(videoItem.id());

        Video video;
        if(found.isPresent()){
            video = found.get();
            video.recollect(snippet.title(), snippet.description(), thumbnailUrl);
        } else {
            video = Video.builder()
                    .youtubeAccount(youtubeAccount)
                    .youtubeVideoId(videoItem.id())
                    .title(snippet.title())
                    .description(snippet.description())
                    .thumbnailUrl(thumbnailUrl)
                    .publishedAt(toLocalDateTime(snippet.publishedAt()))
                    .build();

            videoRepository.save(video);
        }

        // 4. VideoStats insert
        //    - video를 넣어서 builder → save
        VideoStats videoStats = VideoStats.builder()
                .video(video)
                .viewCount(toLong(statistics.viewCount()))
                .likeCount(toLong(statistics.likeCount()))
                .commentCount(toLong(statistics.commentCount()))
                .build();


        videoStatsRepository.save(videoStats);
        return video;
        
    }


    private void saveComments(String accessToken, Video video){

        YoutubeCommentResponse response;
        try{
            response = youtubeGoogleClient.fetchComments(accessToken, video.getYoutubeVideoId());
        } catch (HttpClientErrorException.Forbidden e){
            if(e.getResponseBodyAsString().contains("commentsDisabled")) {
                log.info("댓글 막힌 영상 건너뜀: {}", video.getTitle());
                return;
            }
            throw e; //다른 403(숸한 부족 등)은 진짜 문제니까 디시 던짐.
        }

        List<YoutubeCommentResponse.Item> items = (response.items() == null) ? List.of() : response.items();

        for (YoutubeCommentResponse.Item item : items) {
            saveComment(video,item);
        }
    }

    private void saveComment(Video video, YoutubeCommentResponse.Item item){
        YoutubeCommentResponse.TopLevelComment top = item.snippet().topLevelComment();
        YoutubeCommentResponse.CommentSnippet snippet = top.snippet();

        // 작성자 채널 ID (탈퇴 계정이면 null일 수 있음)
        String authorChannelId = null;
        if (snippet.authorChannelId() != null){
            authorChannelId = snippet.authorChannelId().value();
        }

        Optional<Comment> found = commentRepository.findByYoutubeCommentId(top.id());
        if(found.isPresent()){
            found.get().update(snippet.textOriginal(), snippet.likeCount());
        } else {
            Comment comment = Comment.builder()
                    .video(video)
                    .youtubeCommentId(top.id())
                    .authorName(snippet.authorDisplayName())
                    .authorChannelId(authorChannelId)
                    .content(snippet.textOriginal())
                    .likeCount(snippet.likeCount())
                    .publishedAt(toLocalDateTime(snippet.publishedAt()))
                    .build();
            commentRepository.save(comment);
        }
    }


    private Long toLong(String value){
        return Optional.ofNullable(value).map(Long::valueOf).orElse(null);
    }

    private LocalDateTime toLocalDateTime(String value){
        return Optional.ofNullable(value)
                .map(v -> LocalDateTime.ofInstant(Instant.parse(v), ZoneId.of("Asia/Seoul")))
                .orElse(null);
    }
}
