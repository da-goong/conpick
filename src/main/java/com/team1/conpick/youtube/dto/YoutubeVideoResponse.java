package com.team1.conpick.youtube.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YoutubeVideoResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String id, Snippet snippet, Statistics statistics){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Snippet(String publishedAt, String title, String description, Thumbnails thumbnails){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Thumbnails(@JsonProperty("default") Thumbnail defaultThumbnails, Thumbnail medium, Thumbnail high){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Thumbnail(String url){}


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Statistics(String viewCount, String likeCount, String commentCount){}
}
