package com.team1.conpick.data.collector;

import com.team1.conpick.data.Video;
import com.team1.conpick.data.VideoStats;
import com.team1.conpick.data.channel.ChannelStats;
import com.team1.conpick.data.channel.ChannelStatsRepository;
import com.team1.conpick.data.video.VideoRepository;
import com.team1.conpick.data.video.VideoStatsRepository;
import com.team1.conpick.global.exception.YoutubeChannelNotFoundException;
import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeGoogleClient;
import com.team1.conpick.youtube.YoutubeService;
import com.team1.conpick.youtube.dto.YoutubeChannelResponse;
import com.team1.conpick.youtube.dto.YoutubePlaylistItemResponse;
import com.team1.conpick.youtube.dto.YoutubeVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        //video API : 영상 상세 정보.
        // 1. videoIds가 비어 있으면 videos API를 부르지 않기 (영상 0개 채널)
        if (videoIds == null || videoIds.isEmpty()){
            return;
        }

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
            saveVideo(youtubeAccount, videoItem);
        }


    }

    private void saveVideo(YoutubeAccount youtubeAccount, YoutubeVideoResponse.Item videoItem){
        // 1. snippet, statistics 꺼내서 변수에 담기
        YoutubeVideoResponse.Snippet snippet= videoItem.snippet();
        YoutubeVideoResponse.Statistics statistics = videoItem.statistics();

        // 2. 썸네일 url 고르기 (medium 추천, thumbnails가 null일 수 있음 주의)
        String thumbnailUrl = null;
        if(snippet.thumbnails() != null && snippet.thumbnails().medium() != null){
            thumbnailUrl = snippet.thumbnails().medium().url();
        }

        // 3. upsert
        //    - findByYoutubeVideoId로 찾기 → 결과를 변수에 담기
        //    - 있으면: 꺼내서 recollect(...) 호출
        //    - 없으면: Video.builder()...build() → save
        //    - 어느 쪽이든 마지막에 Video video 변수에 담겨 있어야 함
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
