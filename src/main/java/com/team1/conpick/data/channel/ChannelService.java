package com.team1.conpick.data.channel;

import com.team1.conpick.data.channel.dto.ChannelResponse;
import com.team1.conpick.data.channel.dto.ChannelStatsResponse;
import com.team1.conpick.global.exception.YoutubeChannelNotFoundException;
import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelStatsRepository channelStatsRepository;
    private final YoutubeAccountRepository youtubeAccountRepository;

    @Transactional(readOnly = true)
    public List<ChannelStatsResponse> getChannelStats(Long userId, Long accountId){
        YoutubeAccount account = youtubeAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new YoutubeChannelNotFoundException("채널을 찾지 못했습니다."));

        List<ChannelStats> channelStatsList = channelStatsRepository.findByYoutubeAccountOrderByCollectedAtAsc(account);

        return channelStatsList.stream()
                .map(ChannelStatsResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> getMyChannels(Long userId) {
        return youtubeAccountRepository.findByUserId(userId).stream()
                .map(ChannelResponse::from)
                .toList();
    }


}
