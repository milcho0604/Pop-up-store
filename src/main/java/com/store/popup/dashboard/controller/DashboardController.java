package com.store.popup.dashboard.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.dashboard.dto.*;
import com.store.popup.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 대시보드 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 전체 대시보드 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<CommonResDto> getDashboardStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "대시보드 통계를 조회합니다.", stats);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 인기 팝업 순위 - 조회수 기준
     */
    @GetMapping("/popular/views")
    public ResponseEntity<CommonResDto> getPopularPostsByViews(
            @RequestParam(defaultValue = "10") int limit) {
        List<PopularPostDto> popularPosts = dashboardService.getPopularPostsByViews(limit);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회수 기준 인기 팝업 순위를 조회합니다.", popularPosts);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 인기 팝업 순위 - 좋아요 기준
     */
    @GetMapping("/popular/likes")
    public ResponseEntity<CommonResDto> getPopularPostsByLikes(
            @RequestParam(defaultValue = "10") int limit) {
        List<PopularPostDto> popularPosts = dashboardService.getPopularPostsByLikes(limit);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "좋아요 기준 인기 팝업 순위를 조회합니다.", popularPosts);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 인기 팝업 순위 - 평점 기준
     */
    @GetMapping("/popular/rating")
    public ResponseEntity<CommonResDto> getPopularPostsByRating(
            @RequestParam(defaultValue = "10") int limit) {
        List<PopularPostDto> popularPosts = dashboardService.getPopularPostsByRating(limit);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "평점 기준 인기 팝업 순위를 조회합니다.", popularPosts);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 카테고리별 게시글 분포
     */
    @GetMapping("/category-distribution")
    public ResponseEntity<CommonResDto> getCategoryDistribution() {
        List<CategoryDistributionDto> distribution = dashboardService.getCategoryDistribution();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "카테고리별 게시글 분포를 조회합니다.", distribution);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 리뷰 평균 평점 추이
     */
    @GetMapping("/rating-trend")
    public ResponseEntity<CommonResDto> getRatingTrend(
            @RequestParam(defaultValue = "30") int days) {
        List<RatingTrendDto> trend = dashboardService.getRatingTrend(days);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰 평균 평점 추이를 조회합니다.", trend);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
