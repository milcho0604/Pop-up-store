package com.store.popup.qna.dto;

import com.store.popup.qna.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 질문 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {

    private Long questionId;
    private Long postId;
    private String memberEmail;
    private String memberNickname;
    private String content;
    private boolean hasAnswer;
    private AnswerDto answer;
    private LocalDateTime createdAt;

    public static QuestionDto fromEntity(Question question) {
        AnswerDto answerDto = null;
        if (question.getAnswer() != null && question.getAnswer().getDeletedAt() == null) {
            answerDto = AnswerDto.fromEntity(question.getAnswer());
        }

        return QuestionDto.builder()
                .questionId(question.getId())
                .postId(question.getPost().getId())
                .memberEmail(question.getMember().getMemberEmail())
                .memberNickname(question.getMember().getNickname())
                .content(question.getContent())
                .hasAnswer(question.hasAnswer())
                .answer(answerDto)
                .createdAt(question.getCreatedAt())
                .build();
    }
}
