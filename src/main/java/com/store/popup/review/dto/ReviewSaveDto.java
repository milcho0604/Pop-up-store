package com.store.popup.review.dto;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import com.store.popup.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSaveDto {

    private String content;              // 리뷰 내용
    private Integer satisfaction;        // 만족도 (1-5)
    private Integer waitingTime;         // 대기시간 평가 (1-5)
    private Integer photoAvailability;   // 사진 촬영 가능 정도 (1-5)

    public Review toEntity(Post post, Member member, String reviewImgUrl) {
        Review review = Review.builder()
                .post(post)
                .member(member)
                .content(this.content)
                .satisfaction(this.satisfaction)
                .waitingTime(this.waitingTime)
                .photoAvailability(this.photoAvailability)
                .reviewImgUrl(reviewImgUrl)
                .build();

        // 평점 자동 계산
        review.calculateRating();

        return review;
    }
}
