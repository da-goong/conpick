package com.team1.conpick.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_analyses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentAnalyses {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "comment_id", nullable = false, unique = true)
    private Comment comment;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Sentiment sentiment;

    private String topic;

    private Boolean isQuestion;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @Builder
    public CommentAnalyses(Comment comment, Sentiment sentiment, String topic, Boolean isQuestion){
        this.comment = comment;
        this.sentiment = sentiment;
        this.topic = topic;
        this.isQuestion = isQuestion;
        this.analyzedAt = LocalDateTime.now();
    }

    //AI 재분석 시 덮어쓰기.
    public void CommentAnalyses(Sentiment sentiment, String topic, Boolean isQuestion){
        this.sentiment = sentiment;
        this.topic = topic;
        this.isQuestion = isQuestion;
        this.analyzedAt = LocalDateTime.now();
    }

}
