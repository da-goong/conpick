package com.team1.conpick.data.comment;

import com.team1.conpick.data.video.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByYoutubeCommentId(String commentId);
    Page<Comment> findByVideo(Video video, Pageable pageable);
}
