package com.team1.conpick.data.channel;


import com.team1.conpick.data.channel.dto.ChannelResponse;
import com.team1.conpick.data.channel.dto.ChannelStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube/channels")
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/{accountId}/stats")
    public List<ChannelStatsResponse> getChannelStats(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accountId
    ){
        return channelService.getChannelStats(userId, accountId);
    }

    @GetMapping
    public List<ChannelResponse> getMyChannels(@AuthenticationPrincipal Long userId) {
        return channelService.getMyChannels(userId);
    }
}
