package com.store.popup.comment.repository;

import com.store.popup.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    // 기간별 댓글 수 (대시보드용)
    Long countByCreatedAtBetweenAndDeletedAtIsNull(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}