package com.store.popup.dashboard.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인기 팝업 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularPostDto {

    private Long postId;
    private String title;
    private String postImgUrl;
    private Category category;
    private PostStatus status;
    private Long viewCount;
    private Long likeCount;
    private Double averageRating;
    private Long reviewCount;
    private Integer rank;              // 순위
}
