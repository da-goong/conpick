package com.team1.conpick.data;


import com.team1.conpick.youtube.YoutubeAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "youtube_account_id", nullable = false)
    private YoutubeAccount youtubeAccount;

    @Column(nullable = false, unique = true)
    private String youtubeVideoId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    // youtube 업로드 시각 (API 값 그대로 저장, 자동 생성 X)
    private LocalDateTime publishedAt;

    @Builder
    public Video(YoutubeAccount youtubeAccount, String youtubeVideoId, String title, String description, String thumbnailUrl, LocalDateTime publishedAt){
        this.youtubeAccount = youtubeAccount;
        this.youtubeVideoId = youtubeVideoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;

    }

    public void recollect(String title, String description, String thumbnailUrl){
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

}
