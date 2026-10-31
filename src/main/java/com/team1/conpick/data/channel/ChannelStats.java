package com.team1.conpick.data.channel;


import com.team1.conpick.youtube.YoutubeAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_stats", indexes = @Index(columnList = "youtube_account_id, collected_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelStats{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "youtube_account_id", nullable = false)
    private YoutubeAccount youtubeAccount;

    private Long subscriberCount;
    private Long viewCount;
    private Long videoCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime collectedAt;

    @Builder
    public ChannelStats(YoutubeAccount youtubeAccount, Long subscriberCount, Long viewCount, Long videoCount){
        this.youtubeAccount = youtubeAccount;
        this.subscriberCount = subscriberCount;
        this.viewCount = viewCount;
        this.videoCount = videoCount;
    }
}
