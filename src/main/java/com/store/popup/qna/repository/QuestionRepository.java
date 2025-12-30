package com.store.popup.qna.repository;

import com.store.popup.pop.domain.Post;
import com.store.popup.qna.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Post의 모든 질문 조회 (페이징)
    Page<Question> findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(Post post, Pageable pageable);

    // Post의 모든 질문 조회 (리스트)
    List<Question> findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(Post post);

    // 질문 조회 (답변 포함)
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answer WHERE q.id = :id AND q.deletedAt IS NULL")
    Optional<Question> findByIdWithAnswer(@Param("id") Long id);

    // Post의 미답변 질문 개수
    @Query("SELECT COUNT(q) FROM Question q WHERE q.post = :post AND q.deletedAt IS NULL AND (q.answer IS NULL OR q.answer.deletedAt IS NOT NULL)")
    Long countUnansweredQuestions(@Param("post") Post post);
}
