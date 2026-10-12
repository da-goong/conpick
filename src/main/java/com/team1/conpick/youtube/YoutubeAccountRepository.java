package com.team1.conpick.youtube;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface YoutubeAccountRepository extends JpaRepository<YoutubeAccount, Long> {
    Optional<YoutubeAccount> findByChannelId(String channelId);
    List<YoutubeAccount> findByUserId(Long userId);
}
