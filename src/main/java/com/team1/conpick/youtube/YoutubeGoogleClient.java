package com.team1.conpick.youtube;

import com.team1.conpick.youtube.dto.GoogleTokenResponse;
import com.team1.conpick.youtube.dto.YoutubeChannelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class YoutubeGoogleClient {


    private final YoutubeProperties props;

    public GoogleTokenResponse exchangeCode(String code){

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", props.clientId());
        form.add("client_secret", props.clientSecret());
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", props.redirectUri());


        //POST로 uri에, form 형식으로, 이 내용르 담아 보내고, 응답을 이 record로 바꿔서 받아라.
        return RestClient.create()
                .post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    public YoutubeChannelResponse fetchMyChannel(String accessToken){
        return RestClient.create()
                .get()
                .uri("https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics,contentDetails&mine=true")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(YoutubeChannelResponse.class);
    }

    public GoogleTokenResponse refreshAccessToken(String refreshToken){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", props.clientId());
        form.add("client_secret", props.clientSecret());
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);


        //POST로 uri에, form 형식으로, 이 내용르 담아 보내고, 응답을 이 record로 바꿔서 받아라.
        return RestClient.create()
                .post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }




}
