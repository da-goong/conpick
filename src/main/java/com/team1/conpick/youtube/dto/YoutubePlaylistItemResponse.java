package com.team1.conpick.youtube.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record YoutubePlaylistItemResponse(String nextPageToken, List<Item> items){

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(ContentDetails contentDetails){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentDetails(String videoId, String videoPublishedAt){}
}

