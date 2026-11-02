package com.team1.conpick.youtube.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YoutubeCommentResponse(String nextPageToken, List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String id, ThreadSnippet snippet){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ThreadSnippet(String videoId, Long totalReplyCount, TopLevelComment topLevelComment){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TopLevelComment(String id, CommentSnippet snippet){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommentSnippet(String textOriginal, String authorDisplayName, Long likeCount, String publishedAt, String updatedAt, AuthorChannelId authorChannelId){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuthorChannelId(String value){}


}
