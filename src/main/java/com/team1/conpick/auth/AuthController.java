package com.team1.conpick.auth;


import com.team1.conpick.auth.jwt.RefreshToken;
import com.team1.conpick.auth.jwt.RefreshTokenCookieFactory;
import com.team1.conpick.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieFactory cookieFactory;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue("refresh_token") String refreshToken){

        AuthService.TokenPair pair = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.create(pair.refreshToken()).toString())
                .body(Map.of("accessToken", pair.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken){

        if(refreshToken != null) authService.logout(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.delete().toString())
                .build();
    }

    //유튜브 계정 관련
//    @GetMapping("/youtube/connect")
//    public ResponseEntity<Void> connect(@AuthenticationPrincipal Long userId){
//
//
//    }
}
