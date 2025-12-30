package com.store.popup.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 답변 작성 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerSaveDto {

    private String content;
}
