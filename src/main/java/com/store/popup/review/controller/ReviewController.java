package com.store.popup.review.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.review.dto.ReviewResDto;
import com.store.popup.review.dto.ReviewSaveDto;
import com.store.popup.review.dto.ReviewUpdateDto;
import com.store.popup.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/create/{postId}")
    public ResponseEntity<CommonResDto> createReview(
            @PathVariable Long postId,
            @ModelAttribute ReviewSaveDto dto,
            @RequestParam(required = false) MultipartFile reviewImage) {
        ReviewResDto review = reviewService.createReview(postId, dto, reviewImage);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "리뷰가 작성되었습니다.", review);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 리뷰 조회 (단건)
    @GetMapping("/{reviewId}")
    public ResponseEntity<CommonResDto> getReview(@PathVariable Long reviewId) {
        ReviewResDto review = reviewService.getReview(reviewId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰를 조회합니다.", review);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // Post의 모든 리뷰 조회 (페이징)
    @GetMapping("/post/{postId}")
    public ResponseEntity<CommonResDto> getPostReviews(
            @PathVariable Long postId,
            Pageable pageable) {
        Page<ReviewResDto> reviews = reviewService.getPostReviews(postId, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "포스트의 리뷰 목록을 조회합니다.", reviews);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // Post의 모든 리뷰 조회 (전체 리스트)
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<CommonResDto> getPostReviewsList(@PathVariable Long postId) {
        List<ReviewResDto> reviews = reviewService.getPostReviewsList(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "포스트의 전체 리뷰 목록을 조회합니다.", reviews);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내 리뷰 목록 조회 (페이징)
    @GetMapping("/my/list")
    public ResponseEntity<CommonResDto> getMyReviews(Pageable pageable) {
        Page<ReviewResDto> reviews = reviewService.getMyReviews(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 리뷰 목록을 조회합니다.", reviews);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내 리뷰 목록 조회 (전체 리스트)
    @GetMapping("/my/list/all")
    public ResponseEntity<CommonResDto> getMyReviewsList() {
        List<ReviewResDto> reviews = reviewService.getMyReviewsList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 전체 리뷰 목록을 조회합니다.", reviews);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<CommonResDto> updateReview(
            @PathVariable Long reviewId,
            @ModelAttribute ReviewUpdateDto dto,
            @RequestParam(required = false) MultipartFile reviewImage) {
        ReviewResDto review = reviewService.updateReview(reviewId, dto, reviewImage);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰가 수정되었습니다.", review);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResDto> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰가 삭제되었습니다.", reviewId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
