package com.team1.conpick.data;

import com.team1.conpick.data.channel.ChannelStats;
import com.team1.conpick.data.channel.ChannelStatsRepository;
import com.team1.conpick.global.exception.YoutubeChannelNotFoundException;
import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeGoogleClient;
import com.team1.conpick.youtube.YoutubeService;
import com.team1.conpick.youtube.dto.YoutubeChannelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeCollector {

    private final YoutubeService youtubeService;
    private final YoutubeGoogleClient youtubeGoogleClient;
    private final ChannelStatsRepository channelStatsRepository;

    @Transactional
    public void collect(YoutubeAccount youtubeAccount){
        // 1. 유효한 access token 얻기
        String accessToken = youtubeService.getValidAccessToken(youtubeAccount);

        // 2. 내 채널 조회 (fetchMyChannel)
        YoutubeChannelResponse response = youtubeGoogleClient.fetchMyChannel(accessToken);

        // 3. items가 null이거나 비어 있으면 → 예외 (이미 만든 예외 중 맞는 것 재사용)
        // || 앞이 true면 뒤를 아예 실행하지 않음.
        if(response.items() == null || response.items().isEmpty()){
            throw new YoutubeChannelNotFoundException("channel을 찾지 못했습니다.");
        }

        // 4. 첫 번째 item에 statistics 꺼내기
        YoutubeChannelResponse.Statistics stats = response.items().get(0).statistics();
        String subscriberCountStr = stats.subscriberCount();
        String videoCountStr = stats.videoCount();
        String viewCountStr = stats.viewCount();

        // 5. 문자열 통계를 숫자로 변환 (null 안전하게)
        Long subscriberCount = toLong(subscriberCountStr);
        Long videoCount = toLong(videoCountStr);
        Long viewCount = toLong(viewCountStr);

        // 6. ChannelStats를 빌더로 만들고 save
        ChannelStats channelStats= ChannelStats.builder()
                .youtubeAccount(youtubeAccount)
                .subscriberCount(subscriberCount)
                .videoCount(videoCount)
                .viewCount(viewCount)
                .build();

        channelStatsRepository.save(channelStats);
    }

    private Long toLong(String value){
        return Optional.ofNullable(value).map(Long::valueOf).orElse(null);
    }
}
