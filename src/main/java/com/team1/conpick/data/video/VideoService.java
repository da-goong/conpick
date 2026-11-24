package com.team1.conpick.data.video;

import com.team1.conpick.data.video.dto.VideoResponse;
import com.team1.conpick.data.video.dto.VideoStatsResponse;
import com.team1.conpick.global.exception.VideoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoStatsRepository videoStatsRepository;

    @Transactional(readOnly = true)
    public Page<VideoResponse> getMyVideos(Long userId, Pageable pageable){
         Page<Video> videos= videoRepository.findByYoutubeAccount_User_Id(userId, pageable);
         return videos.map(VideoResponse::from);
    }

    @Transactional(readOnly = true)
    public List<VideoStatsResponse> getVideoStats(Long userId, Long videoId){
        Video video = videoRepository.findByIdAndYoutubeAccount_User_Id(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("비디오를 찾지 못했습니다."));

        List<VideoStats> videoStatsList = videoStatsRepository.findByVideoOrderByCollectedAtAsc(video);

        return videoStatsList.stream()
                .map(VideoStatsResponse::from)
                .toList();
    }
}
