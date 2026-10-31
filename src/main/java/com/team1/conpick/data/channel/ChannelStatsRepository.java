package com.team1.conpick.data.channel;

import com.team1.conpick.youtube.YoutubeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelStatsRepository extends JpaRepository<ChannelStats, Long> {

    //이 계정의 가장 최근 통계 1건 조회
    Optional<ChannelStats> findTopByYoutubeAccountOrderByCollectedAtDesc(YoutubeAccount youtubeAccount);
}
