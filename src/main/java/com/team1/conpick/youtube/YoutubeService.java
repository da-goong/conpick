package com.team1.conpick.youtube;

import com.team1.conpick.global.exception.InvalidStateException;
import com.team1.conpick.global.exception.YoutubeChannelAlreadyConnectedException;
import com.team1.conpick.global.exception.YoutubeChannelNotFoundException;
import com.team1.conpick.user.User;
import com.team1.conpick.user.UserRepository;
import com.team1.conpick.youtube.dto.GoogleTokenResponse;
import com.team1.conpick.youtube.dto.YoutubeChannelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YoutubeStateStore stateStore;
    private final YoutubeProperties props;
    private final YoutubeGoogleClient client;
    private final YoutubeAccountRepository youtubeAccountRepository;
    private final UserRepository userRepository;

    //google 인가 URl
    private static final String URL = "https://accounts.google.com/o/oauth2/v2/auth";
    /**
     * GET /auth/youtube/callback?code=abc123&state=xyz789
     * 이 요청은 Google이 브라우저를 우리 서버로 리다이렉트 시킨 거라 JWT 헤더가 없음.
     * 서버 입장에서는 code를 받았는데 어느 유저의 요청인지 알 수가 없음.
     */

    //state: 이 OAuth 요청을 시작한 유저가 누구인지 기억하는 일회용 번호표 / 5-10분, 한번 쓰고 삭제 -> Redis 이용.
    //랜덤 state 생성
    private String createYoutubeState(){
        return UUID.randomUUID().toString();
    }

    public String createAuthorizationUrl(Long userId){
        String state = createYoutubeState();
        stateStore.save(state, userId);

        String url = UriComponentsBuilder
                .fromUriString(URL)
                .queryParam("client_id", props.clientId())
                .queryParam("redirect_uri", props.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", props.scope())
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", state)
                .encode()
                .build()
                .toUriString();

        return url;

    }

    @Transactional
    //** code, access_token, refresh_token, client_secret은 로그에 찍으면 안딤.
    public void connect(String code, String state){
        Long userId = stateStore.consume(state).orElseThrow(() -> new InvalidStateException("유효하지 않은 state입니다."));
        log.info("youtube callback userId={}", userId);

        GoogleTokenResponse token = client.exchangeCode(code);
        log.info("expiresIn={}, hasRefreshToken={}", token.expiresIn(), token.refreshToken() != null);

        YoutubeChannelResponse channels = client.fetchMyChannel(token.accessToken());
        if(channels == null || channels.items() == null || channels.items().isEmpty()){
            throw new YoutubeChannelNotFoundException("이 구글 계정에는 유튜브 채널이 존재하지 않습니다.");
        }

        YoutubeChannelResponse.Item channel = channels.items().get(0);
        log.info("channelId={}, title={}", channel.id(), channel.snippet().title());

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(token.expiresIn());

        Optional<YoutubeAccount> existing = youtubeAccountRepository.findByChannelId(channel.id());

        if(existing.isPresent()){
            YoutubeAccount account = existing.get();

            if (!account.getUser().getId().equals(userId)){
                throw new YoutubeChannelAlreadyConnectedException("이미 다른 계정에 연결된 채널입니다.");
            }

            account.updateTokens(token.accessToken(), token.refreshToken(), expiresAt);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        YoutubeAccount account = YoutubeAccount.builder()
                .user(user)
                .channelId(channel.id())
                .channelTitle(channel.snippet().title())
                .thumbnailUrl(channel.snippet().thumbnails().defaultThumbnail().url())
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .tokenExpiresAt(expiresAt)
                .build();

        youtubeAccountRepository.save(account);

    }

    @Transactional
    public String getValidAccessToken(YoutubeAccount account){

        if(
                account.getTokenExpiresAt().isAfter(LocalDateTime.now().plusMinutes(1))
        ){
        return account.getAccessToken();}

        GoogleTokenResponse token = client.refreshAccessToken(account.getRefreshToken());

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(token.expiresIn());
        account.updateTokens(token.accessToken(), token.refreshToken(), expiresAt);

        youtubeAccountRepository.save(account);

        return token.accessToken();





    }

}
