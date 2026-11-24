package com.team1.conpick.data.video;

import com.team1.conpick.data.video.dto.VideoResponse;
import com.team1.conpick.data.video.dto.VideoStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/videos")
public class VideoController {
    private final VideoService videoService;

    @GetMapping("")
    public Page<VideoResponse> getMyVideos(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return videoService.getMyVideos(userId, pageable);
    }

    @GetMapping("/{videoId}/stats")
    public List<VideoStatsResponse> getMyVideosStats(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long videoId
    ){
        return videoService.getVideoStats(userId, videoId);
    }

}
