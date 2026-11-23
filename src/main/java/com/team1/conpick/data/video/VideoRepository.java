package com.team1.conpick.data.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByYoutubeVideoId(String youtubeVideoId);
    Page<Video> findByYoutubeAccount_User_Id(Long userId, Pageable pageable); //pageable을 맨 마지막 파라미터로 넣으면 스프링이 알아서 LIMIT/OFFSET/ORDER BY를 붙여줌.
}
