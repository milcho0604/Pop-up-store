package com.store.popup.dashboard.dto;

import com.store.popup.common.enumdir.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카테고리별 게시글 분포 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDistributionDto {

    private Category category;
    private Long postCount;
    private Double percentage;
}
