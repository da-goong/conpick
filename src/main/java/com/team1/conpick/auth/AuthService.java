package com.team1.conpick.auth;

import com.team1.conpick.auth.jwt.JwtProvider;
import com.team1.conpick.auth.jwt.RefreshToken;
import com.team1.conpick.auth.jwt.RefreshTokenRepository;
import com.team1.conpick.global.exception.InvalidRefreshTokenException;
import com.team1.conpick.user.Provider;
import com.team1.conpick.user.User;
import com.team1.conpick.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenrepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    @Transactional
    public User loginOrSignUp(OAuth2User oAuth2User){

        // Google이 준 정보 꺼내기
        String sub = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        //1) 이미 Google로 연결된 회원
        Optional<User> bySub= userRepository.findByProviderAndProviderId(Provider.google, sub);

        // 기존 회원이면: 프로필 갱신 후 반환(로그인)
        if(bySub.isPresent()){
            User user = bySub.get();
            user.updateProfile(name, picture);
            return user;
        }

        //3-b. 없으면 : 새로 만들어 저장 (회원가입)
        User newUser = User.builder()
                .provider(Provider.google)
                .providerId(sub)
                .email(email)
                .name(name)
                .profileImageUrl(picture)
                .build();

        return userRepository.save(newUser);
    }

    // refresh 토큰 생성.
    @Transactional
    public String createRefreshToken(Long userId){
        //랜덤 문자열
        String raw = UUID.randomUUID() + "." + UUID.randomUUID(); //랜덤 문자열.
        refreshTokenrepository.save(
                new RefreshToken(userId, hash(raw), LocalDateTime.now().plusDays(14)));

        return raw; //원문은 쿠키로 사용자에게 전달.
    }

    // 사용자가 AccessToken이 만료되어 다시 요청 시
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public TokenPair refresh(String raw){
        RefreshToken saved = refreshTokenrepository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new InvalidRefreshTokenException("유효하지 않은 토큰"));
        refreshTokenrepository.delete(saved); //기존 Refresh Token 삭제.

        if (saved.isExpired()) throw new InvalidRefreshTokenException("만료된 토큰");

        User user = userRepository.findById(saved.getUserId()).orElseThrow();
        //새 accessToken + 새 RefreshToken 발급.
        return new TokenPair(jwtProvider.createToken(user), createRefreshToken(user.getId()));
    }

    //logout시 사용자가 가진 refreshToken 삭제.
    @Transactional
    public void logout(String raw){
        refreshTokenrepository.findByTokenHash(hash(raw))
                .ifPresent(refreshTokenrepository::delete);
    }

    //SHA-256 해시
    public String hash(String raw){
        try{
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e){
            throw new IllegalStateException(e);
        }
    }

    public record TokenPair(String accessToken, String refreshToken){}
}
