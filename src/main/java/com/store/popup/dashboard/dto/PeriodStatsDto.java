package com.store.popup.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 기간별 통계 DTO (일일/주간/월간)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodStatsDto {

    private Long newPosts;          // 신규 게시글 수
    private Long newMembers;        // 신규 회원 수
    private Long newReviews;        // 신규 리뷰 수
    private Long newComments;       // 신규 댓글 수
    private Long totalViews;        // 총 조회수
    private Long totalLikes;        // 총 좋아요 수
}
