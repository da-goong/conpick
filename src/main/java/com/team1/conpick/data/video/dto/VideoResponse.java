package com.team1.conpick.data.video.dto;

import com.team1.conpick.data.video.Video;

import java.time.LocalDateTime;

public record VideoResponse (
        Long id,
        String youtubeVideoId,
        String title,
        String thumbnailUrl,
        LocalDateTime publishedAt
){
    public static VideoResponse from(Video video){
        return new VideoResponse(
                video.getId(),
                video.getYoutubeVideoId(),
                video.getTitle(),
                video.getThumbnailUrl(),
                video.getPublishedAt()
        );
    }
}
