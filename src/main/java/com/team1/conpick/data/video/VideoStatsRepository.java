package com.team1.conpick.data.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoStatsRepository extends JpaRepository<VideoStats, Long> {
    List<VideoStats> findByVideoOrderByCollectedAtAsc(Video video);
}
