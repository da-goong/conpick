package com.team1.conpick.youtube;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

//ConfigurationProperties : yml의 키와 이름이 같은 필드에 값을 자동으로 넣어주는 기능.
//필드만 선언하면 매핑은 스프링이 해줌.
@ConfigurationProperties(prefix = "app.youtube")
public record YoutubeProperties (
    String clientId,
    String clientSecret,
    String redirectUri,
    String scope,
    String frontendRedirectUri
){}
