package com.store.popup.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 리뷰 평균 평점 추이 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingTrendDto {

    private LocalDate date;
    private Double averageRating;
    private Long reviewCount;
}
