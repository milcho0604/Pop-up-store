package com.store.popup.dashboard.service;

import com.store.popup.comment.repository.CommentRepository;
import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.Role;
import com.store.popup.dashboard.dto.*;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.pop.service.PostMetricsService;
import com.store.popup.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PostMetricsService postMetricsService;
    private final RedisTemplate<String, Object> redisTemplateDb7;

    /**
     * 전체 대시보드 통계 조회
     */
    public DashboardStatsDto getDashboardStats() {
        checkAdminRole(getCurrentMember());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfMonth = now.minusDays(30);

        return DashboardStatsDto.builder()
                .totalPosts(postRepository.count())
                .totalMembers(memberRepository.count())
                .totalReviews(reviewRepository.count())
                .totalComments(commentRepository.count())
                .daily(getPeriodStats(startOfToday, now))
                .weekly(getPeriodStats(startOfWeek, now))
                .monthly(getPeriodStats(startOfMonth, now))
                .build();
    }

    /**
     * 기간별 통계
     */
    private PeriodStatsDto getPeriodStats(LocalDateTime startDate, LocalDateTime endDate) {
        Long newPosts = postRepository.countByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);
        Long newMembers = memberRepository.countByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);
        Long newReviews = reviewRepository.countByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);
        Long newComments = commentRepository.countByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);

        // Redis에서 조회수/좋아요 합계 계산
        List<Post> posts = postRepository.findByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);
        Long totalViews = posts.stream()
                .mapToLong(post -> postMetricsService.getPostViews(post.getId()))
                .sum();
        Long totalLikes = posts.stream()
                .mapToLong(post -> postMetricsService.getPostLikesCount(post.getId()))
                .sum();

        return PeriodStatsDto.builder()
                .newPosts(newPosts)
                .newMembers(newMembers)
                .newReviews(newReviews)
                .newComments(newComments)
                .totalViews(totalViews)
                .totalLikes(totalLikes)
                .build();
    }

    /**
     * 인기 팝업 순위 (조회수 기준)
     */
    public List<PopularPostDto> getPopularPostsByViews(int limit) {
        checkAdminRole(getCurrentMember());

        List<Post> allPosts = postRepository.findByDeletedAtIsNull();

        List<PopularPostDto> sortedPosts = allPosts.stream()
                .map(post -> {
                    Long viewCount = postMetricsService.getPostViews(post.getId());
                    Long likeCount = postMetricsService.getPostLikesCount(post.getId());
                    return PopularPostDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .postImgUrl(post.getPostImgUrl())
                            .category(post.getCategory())
                            .status(post.getStatus())
                            .viewCount(viewCount)
                            .likeCount(likeCount)
                            .averageRating(post.getAverageRating())
                            .reviewCount(post.getReviewCount())
                            .build();
                })
                .sorted(Comparator.comparing(PopularPostDto::getViewCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // 순위 추가
        for (int i = 0; i < sortedPosts.size(); i++) {
            sortedPosts.get(i).setRank(i + 1);
        }
        return sortedPosts;
    }

    /**
     * 인기 팝업 순위 (좋아요 기준)
     */
    public List<PopularPostDto> getPopularPostsByLikes(int limit) {
        checkAdminRole(getCurrentMember());

        List<Post> allPosts = postRepository.findByDeletedAtIsNull();

        List<PopularPostDto> sortedPosts = allPosts.stream()
                .map(post -> {
                    Long viewCount = postMetricsService.getPostViews(post.getId());
                    Long likeCount = postMetricsService.getPostLikesCount(post.getId());
                    return PopularPostDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .postImgUrl(post.getPostImgUrl())
                            .category(post.getCategory())
                            .status(post.getStatus())
                            .viewCount(viewCount)
                            .likeCount(likeCount)
                            .averageRating(post.getAverageRating())
                            .reviewCount(post.getReviewCount())
                            .build();
                })
                .sorted(Comparator.comparing(PopularPostDto::getLikeCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // 순위 추가
        for (int i = 0; i < sortedPosts.size(); i++) {
            sortedPosts.get(i).setRank(i + 1);
        }
        return sortedPosts;
    }

    /**
     * 인기 팝업 순위 (평점 기준)
     */
    public List<PopularPostDto> getPopularPostsByRating(int limit) {
        checkAdminRole(getCurrentMember());

        List<Post> allPosts = postRepository.findByDeletedAtIsNull();

        List<PopularPostDto> sortedPosts = allPosts.stream()
                .filter(post -> post.getAverageRating() != null && post.getAverageRating() > 0)
                .map(post -> {
                    Long viewCount = postMetricsService.getPostViews(post.getId());
                    Long likeCount = postMetricsService.getPostLikesCount(post.getId());
                    return PopularPostDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .postImgUrl(post.getPostImgUrl())
                            .category(post.getCategory())
                            .status(post.getStatus())
                            .viewCount(viewCount)
                            .likeCount(likeCount)
                            .averageRating(post.getAverageRating())
                            .reviewCount(post.getReviewCount())
                            .build();
                })
                .sorted(Comparator.comparing(PopularPostDto::getAverageRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // 순위 추가
        for (int i = 0; i < sortedPosts.size(); i++) {
            sortedPosts.get(i).setRank(i + 1);
        }
        return sortedPosts;
    }

    /**
     * 카테고리별 게시글 분포
     */
    public List<CategoryDistributionDto> getCategoryDistribution() {
        checkAdminRole(getCurrentMember());

        List<Post> allPosts = postRepository.findByDeletedAtIsNull();
        long totalCount = allPosts.size();

        Map<Category, Long> categoryCount = allPosts.stream()
                .filter(post -> post.getCategory() != null)
                .collect(Collectors.groupingBy(Post::getCategory, Collectors.counting()));

        return categoryCount.entrySet().stream()
                .map(entry -> CategoryDistributionDto.builder()
                        .category(entry.getKey())
                        .postCount(entry.getValue())
                        .percentage(totalCount > 0 ? (entry.getValue() * 100.0 / totalCount) : 0.0)
                        .build())
                .sorted(Comparator.comparing(CategoryDistributionDto::getPostCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 평균 평점 추이 (최근 30일)
     */
    public List<RatingTrendDto> getRatingTrend(int days) {
        checkAdminRole(getCurrentMember());

        LocalDate today = LocalDate.now();
        List<RatingTrendDto> trends = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Double avgRating = reviewRepository.calculateAverageRatingByPeriod(startOfDay, endOfDay);
            Long reviewCount = reviewRepository.countByCreatedAtBetweenAndDeletedAtIsNull(startOfDay, endOfDay);

            trends.add(RatingTrendDto.builder()
                    .date(date)
                    .averageRating(avgRating != null ? avgRating : 0.0)
                    .reviewCount(reviewCount)
                    .build());
        }

        return trends;
    }

    /**
     * 관리자 권한 확인
     */
    private void checkAdminRole(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
