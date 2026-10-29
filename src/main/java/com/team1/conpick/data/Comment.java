package com.team1.conpick.data;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    //Youtube가 준 댓글 ID
    @Column(unique = true, nullable = false)
    private String youtubeCommentId;

    //대댓글이면 부모 댓글의 Youtube ID
    private String parentCommentId;

    private String authorName;
    private String authorChannelId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer likeCount;
    private LocalDateTime publishedAt;

    @Builder
    public Comment(Video video, String youtubeCommentId, String parentCommentId, String authorName, String authorChannelId, String content, Integer likeCount, LocalDateTime publishedAt){
        this.video = video;
        this.youtubeCommentId = youtubeCommentId;
        this.parentCommentId = parentCommentId;
        this.authorName = authorName;
        this.authorChannelId = authorChannelId;
        this.content = content;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
    }

    // 재수집 시 수정된 내용 좋아요 수 갱신
    public void update(String content, Integer likeCount){
        this.content = content;
        this.likeCount = likeCount;
    }

}
