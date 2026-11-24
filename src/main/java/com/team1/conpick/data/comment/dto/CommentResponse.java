package com.team1.conpick.data.comment.dto;

import com.team1.conpick.data.comment.Comment;
import com.team1.conpick.data.video.Video;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String authorName,
        String content,
        Long likeCount,
        LocalDateTime publishedAt) {
    public static CommentResponse from(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getAuthorName(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getPublishedAt()
        );
    }
}