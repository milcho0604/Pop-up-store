package com.store.popup.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팝업 스토어 리뷰/평점 엔티티
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "review",
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "member_id"}))
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    /**
     * 리뷰 내용
     */
    @Column(name = "content", length = 2000)
    private String content;

    /**
     * 만족도 (1-5)
     */
    @Column(name = "satisfaction", nullable = false)
    private Integer satisfaction;

    /**
     * 대기시간 평가 (1-5)
     * 1: 매우 길었음, 5: 거의 없었음
     */
    @Column(name = "waiting_time", nullable = false)
    private Integer waitingTime;

    /**
     * 사진 촬영 가능 정도 (1-5)
     * 1: 촬영 불가, 5: 자유롭게 가능
     */
    @Column(name = "photo_availability", nullable = false)
    private Integer photoAvailability;

    /**
     * 전체 평점 (satisfaction, waitingTime, photoAvailability의 평균)
     */
    @Column(name = "rating", nullable = false)
    private Double rating;

    /**
     * 리뷰 이미지 URL
     */
    @Column(name = "review_img_url", length = 500)
    private String reviewImgUrl;

    /**
     * 전체 평점 계산 (3개 항목의 평균)
     */
    public void calculateRating() {
        if (satisfaction != null && waitingTime != null && photoAvailability != null) {
            this.rating = (satisfaction + waitingTime + photoAvailability) / 3.0;
        }
    }

    /**
     * 리뷰 수정
     */
    public void update(String content, Integer satisfaction, Integer waitingTime,
                      Integer photoAvailability, String reviewImgUrl) {
        if (content != null) {
            this.content = content;
        }
        if (satisfaction != null) {
            validateRating(satisfaction);
            this.satisfaction = satisfaction;
        }
        if (waitingTime != null) {
            validateRating(waitingTime);
            this.waitingTime = waitingTime;
        }
        if (photoAvailability != null) {
            validateRating(photoAvailability);
            this.photoAvailability = photoAvailability;
        }
        if (reviewImgUrl != null) {
            this.reviewImgUrl = reviewImgUrl;
        }

        // 평점 재계산
        calculateRating();
    }

    /**
     * 평점 유효성 검증 (1-5 범위)
     */
    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1-5 사이여야 합니다.");
        }
    }

    /**
     * 리뷰 이미지 업데이트
     */
    public void updateReviewImage(String reviewImgUrl) {
        this.reviewImgUrl = reviewImgUrl;
    }
}
