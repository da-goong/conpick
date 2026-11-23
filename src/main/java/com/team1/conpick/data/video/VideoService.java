package com.team1.conpick.data.video;

import com.team1.conpick.data.video.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public Page<VideoResponse> getMyVideos(Long userId, Pageable pageable){
         Page<Video> videos= videoRepository.findByYoutubeAccount_User_Id(userId, pageable);
         return videos.map(VideoResponse::from);
    }
}
