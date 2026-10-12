package com.team1.conpick.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final YoutubeProperties props;

    @GetMapping("/auth/youtube/callback")
    public ResponseEntity<Void> callback(@RequestParam(required = false) String code, @RequestParam(required = false) String error, @RequestParam(required = false) String state){

        // 1. 사용자 취소
        // google이 error=access_denied를 보냄.
        if(error != null){
            return redirectToFront("cancelled");
        }

        // 2. 비정상 요청
        //error도 없고 code도 없음.
        if(code == null || state == null){
            return redirectToFront("error");
        }

        try{
            youtubeService.connect(code, state);
            return redirectToFront("success");
        } catch (RuntimeException e){
            log.warn("연결 실패", e);
            return redirectToFront("error");
        }
    }

    @GetMapping("/test/youtube-url")
    public String test() {
        return youtubeService.createAuthorizationUrl(1L);
    }

    @GetMapping("/auth/youtube/connect")
    public Map<String, String> connect(@AuthenticationPrincipal Long userId){
        return Map.of("url", youtubeService.createAuthorizationUrl(userId));

    }

    private ResponseEntity<Void> redirectToFront(String status) {
        String url = UriComponentsBuilder
                .fromUriString(props.frontendRedirectUri())
                .queryParam("status", status)
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }
}
