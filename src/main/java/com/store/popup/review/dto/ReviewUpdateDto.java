package com.store.popup.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateDto {

    private String content;              // 리뷰 내용
    private Integer satisfaction;        // 만족도 (1-5)
    private Integer waitingTime;         // 대기시간 평가 (1-5)
    private Integer photoAvailability;   // 사진 촬영 가능 정도 (1-5)
}
