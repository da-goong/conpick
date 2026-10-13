package com.team1.conpick.data;


import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_stats")
public class Channel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne @JoinColumn(name = "youtube_accounts", nullable = false)
    Long youtubeAccountId;

    Long subscriberCount;
    Long viewCount;
    Integer videoCount;

    @CreatedDate
    private LocalDateTime collectedAt;

    @Builder
    public Channel(Long youtubeAccountId, Long subscriberCount, Long viewCount, Integer videoCount){
        this.youtubeAccountId = youtubeAccountId;
        this.subscriberCount = subscriberCount;
        this.viewCount = viewCount;
        this.videoCount = videoCount;
    }
}
