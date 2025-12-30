package com.store.popup.qna.repository;

import com.store.popup.qna.domain.Answer;
import com.store.popup.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 질문에 대한 답변 조회
    Optional<Answer> findByQuestionAndDeletedAtIsNull(Question question);
}
