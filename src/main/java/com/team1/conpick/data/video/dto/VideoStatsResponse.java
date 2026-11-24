package com.team1.conpick.data.video.dto;

import com.team1.conpick.data.video.VideoStats;

import java.time.LocalDateTime;

public record VideoStatsResponse (
        Long viewCount,
        Long likeCount,
        Long commentCount,
        LocalDateTime collectedAt
){
    public static VideoStatsResponse from(VideoStats videoStats) {
        return new VideoStatsResponse(
                videoStats.getViewCount(),
                videoStats.getLikeCount(),
                videoStats.getCommentCount(),
                videoStats.getCollectedAt()
        );
    }
}
