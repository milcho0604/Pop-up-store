package com.store.popup.review.service;

import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.review.domain.Review;
import com.store.popup.review.dto.ReviewResDto;
import com.store.popup.review.dto.ReviewSaveDto;
import com.store.popup.review.dto.ReviewUpdateDto;
import com.store.popup.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;

    // 리뷰 작성
    public ReviewResDto createReview(Long postId, ReviewSaveDto dto, MultipartFile reviewImage) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByPostAndMemberAndDeletedAtIsNull(post, member)) {
            throw new IllegalArgumentException("이미 리뷰를 작성하셨습니다.");
        }

        // 삭제된 포스트는 리뷰 작성 불가
        if (post.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 포스트에는 리뷰를 작성할 수 없습니다.");
        }

        // 이미지 업로드
        String reviewImgUrl = null;
        if (reviewImage != null && !reviewImage.isEmpty()) {
            reviewImgUrl = s3ClientFileUpload.upload(reviewImage);
        }

        // 리뷰 생성
        Review review = dto.toEntity(post, member, reviewImgUrl);
        Review savedReview = reviewRepository.save(review);

        // Post의 평균 평점 업데이트
        updatePostRating(postId);

        return ReviewResDto.fromEntity(savedReview);
    }

    // 리뷰 조회 (단건)
    @Transactional(readOnly = true)
    public ReviewResDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰입니다."));

        if (review.getDeletedAt() != null) {
            throw new EntityNotFoundException("삭제된 리뷰입니다.");
        }

        return ReviewResDto.fromEntity(review);
    }

    // Post의 모든 리뷰 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<ReviewResDto> getPostReviews(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        Page<Review> reviews = reviewRepository.findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(post, pageable);
        return reviews.map(ReviewResDto::fromEntity);
    }

    // Post의 모든 리뷰 조회 (리스트)
    @Transactional(readOnly = true)
    public List<ReviewResDto> getPostReviewsList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        List<Review> reviews = reviewRepository.findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(post);
        return reviews.stream()
                .map(ReviewResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 내 리뷰 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<ReviewResDto> getMyReviews(Pageable pageable) {
        Member member = getCurrentMember();
        Page<Review> reviews = reviewRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member, pageable);
        return reviews.map(ReviewResDto::fromEntity);
    }

    // 내 리뷰 목록 조회 (리스트)
    @Transactional(readOnly = true)
    public List<ReviewResDto> getMyReviewsList() {
        Member member = getCurrentMember();
        List<Review> reviews = reviewRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);
        return reviews.stream()
                .map(ReviewResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 리뷰 수정
    public ReviewResDto updateReview(Long reviewId, ReviewUpdateDto dto, MultipartFile reviewImage) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰입니다."));

        // 본인만 수정 가능
        checkReviewOwnership(review);

        // 이미지 업로드
        String reviewImgUrl = review.getReviewImgUrl();
        if (reviewImage != null && !reviewImage.isEmpty()) {
            reviewImgUrl = s3ClientFileUpload.upload(reviewImage);
        }

        // 리뷰 업데이트 (dirty checking)
        review.update(dto.getContent(), dto.getSatisfaction(), dto.getWaitingTime(),
                dto.getPhotoAvailability(), reviewImgUrl);

        // Post의 평균 평점 업데이트
        updatePostRating(review.getPost().getId());

        return ReviewResDto.fromEntity(review);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰입니다."));

        // 본인만 삭제 가능
        checkReviewOwnership(review);

        Long postId = review.getPost().getId();

        // Soft delete
        review.updateDeleteAt();

        // Post의 평균 평점 업데이트
        updatePostRating(postId);
    }

    // Post의 평균 평점 업데이트
    private void updatePostRating(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        Double averageRating = reviewRepository.calculateAverageRating(postId);
        Long reviewCount = reviewRepository.countByPostId(postId);

        // Post 엔티티 업데이트 (dirty checking)
        post.updateRatingInfo(averageRating, reviewCount);
    }

    // 리뷰 소유권 확인
    private void checkReviewOwnership(Review review) {
        Member currentMember = getCurrentMember();

        if (!review.getMember().getId().equals(currentMember.getId())) {
            throw new AccessDeniedException("본인의 리뷰만 수정/삭제할 수 있습니다.");
        }
    }

    // 현재 로그인한 회원 조회
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
