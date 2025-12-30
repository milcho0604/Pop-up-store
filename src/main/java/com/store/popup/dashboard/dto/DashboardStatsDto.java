package com.store.popup.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드 전체 통계 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {

    // 전체 통계
    private Long totalPosts;           // 전체 게시글 수
    private Long totalMembers;         // 전체 회원 수
    private Long totalReviews;         // 전체 리뷰 수
    private Long totalComments;        // 전체 댓글 수

    // 기간별 통계
    private PeriodStatsDto daily;      // 일일 통계
    private PeriodStatsDto weekly;     // 주간 통계
    private PeriodStatsDto monthly;    // 월간 통계
}
