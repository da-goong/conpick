package com.team1.conpick.data.channel.dto;

import com.team1.conpick.data.channel.ChannelStats;

import java.time.LocalDateTime;

public record ChannelStatsResponse (
        Long subscriberCount,
        Long viewCount,
        Long videoCount,
        LocalDateTime collectedAt
){
    public static ChannelStatsResponse from(ChannelStats channelStats){
        return new ChannelStatsResponse(
          channelStats.getSubscriberCount(),
          channelStats.getViewCount(),
          channelStats.getVideoCount(),
          channelStats.getCollectedAt()
        );
    }
}
