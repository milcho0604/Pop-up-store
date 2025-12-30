package com.store.popup.qna.dto;

import com.store.popup.qna.domain.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 답변 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDto {

    private Long answerId;
    private String memberEmail;
    private String memberNickname;
    private String content;
    private LocalDateTime createdAt;

    public static AnswerDto fromEntity(Answer answer) {
        return AnswerDto.builder()
                .answerId(answer.getId())
                .memberEmail(answer.getMember().getMemberEmail())
                .memberNickname(answer.getMember().getNickname())
                .content(answer.getContent())
                .createdAt(answer.getCreatedAt())
                .build();
    }
}
