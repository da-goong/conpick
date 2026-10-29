package com.team1.conpick.youtube.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YoutubeChannelResponse(List<Item> items){

    //Json에 내가 선언하지 않은 필드가 있어도 에러 내지 말고 무시.
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String id, Snippet snippet, Statistics statistics, ContentDetails contentDetails){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Snippet(String title, String customUrl, Thumbnails thumbnails){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Statistics(String subscriberCount, String videoCount, String viewCount){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Thumbnails(@JsonProperty("default") Default defaultThumbnail){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Default(String url){}

    //video 업로드 재생목록 ID 수집 추가
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentDetails(RelatedPlaylists relatedPlaylists){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RelatedPlaylists(String uploads){}

}
