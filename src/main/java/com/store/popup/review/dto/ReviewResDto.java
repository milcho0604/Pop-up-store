package com.store.popup.review.dto;

import com.store.popup.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResDto {

    private Long reviewId;
    private Long postId;
    private String postTitle;
    private Long memberId;
    private String memberNickname;
    private String memberProfileImg;
    private String content;
    private Integer satisfaction;
    private Integer waitingTime;
    private Integer photoAvailability;
    private Double rating;               // 전체 평점
    private String reviewImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewResDto fromEntity(Review review) {
        return ReviewResDto.builder()
                .reviewId(review.getId())
                .postId(review.getPost().getId())
                .postTitle(review.getPost().getTitle())
                .memberId(review.getMember().getId())
                .memberNickname(review.getMember().getNickname())
                .memberProfileImg(review.getMember().getProfileImgUrl())
                .content(review.getContent())
                .satisfaction(review.getSatisfaction())
                .waitingTime(review.getWaitingTime())
                .photoAvailability(review.getPhotoAvailability())
                .rating(review.getRating())
                .reviewImgUrl(review.getReviewImgUrl())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
