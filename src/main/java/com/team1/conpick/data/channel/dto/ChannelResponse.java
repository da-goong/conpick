package com.team1.conpick.data.channel.dto;

import com.team1.conpick.youtube.YoutubeAccount;

import java.time.LocalDateTime;

public record ChannelResponse(
        Long accountId,
        String channelId,
        String channelTitle,
        String thumbnailUrl,
        LocalDateTime connectedAt
) {
    public static ChannelResponse from(YoutubeAccount account) {
        return new ChannelResponse(
                account.getId(),
                account.getChannelId(),
                account.getChannelTitle(),
                account.getThumbnailUrl(),
                account.getConnectedAt()
        );
    }
}