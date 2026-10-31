package com.team1.conpick.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_stats", indexes = @Index(columnList = "video_id, collected_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoStats {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;

    @CreationTimestamp @Column(nullable = false, updatable = false)
    private LocalDateTime collectedAt;

    @Builder
    public VideoStats(Video video, Long viewCount, Long likeCount, Long commentCount){
        this.video = video;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }


}
