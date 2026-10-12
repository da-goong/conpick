package com.team1.conpick.youtube;


import com.team1.conpick.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "youtube_accounts")
public class YoutubeAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //한 유저가 여러 채널 연결 가능.
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String channelId;

    @Column(nullable = false)
    private String channelTitle;

    private String thumbnailUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    private LocalDateTime tokenExpiresAt;
    private LocalDateTime connectedAt;

    @Builder
    public YoutubeAccount(User user, String channelId, String channelTitle, String thumbnailUrl, String accessToken, String refreshToken, LocalDateTime tokenExpiresAt){
        this.user = user;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.connectedAt = LocalDateTime.now();
    }

    public void updateTokens(String accessToken, String refreshToken, LocalDateTime tokenExpiresAt){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }



}
