package com.store.popup.pop.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.pop.dto.*;
import com.store.popup.pop.service.PostDetailService;
import com.store.popup.pop.service.PostMetricsService;
import com.store.popup.pop.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("post")
public class PostController {
    private final PostService postService;
    private final PostMetricsService postMetricsService;
    private final PostDetailService postDetailService;

    // 팝업 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> postList(){
        List<PostListDto> postListDtos = postService.postList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post 목록을 조회합니다.", postListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    // 팝업 목록 조회
    @GetMapping("/list/city")
    public ResponseEntity<?> findPopupsByCity(@RequestParam String city){
        List<PostListDto> postListDtos = postService.findPopupsByCity(city);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post 목록을 조회합니다.", postListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    // 팝업 목록 조회
    @GetMapping("/list/dong")
    public ResponseEntity<?> findPopupsByDong(@RequestParam String dong){
        List<PostListDto> postListDtos = postService.findPopupsByDong(dong);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post 목록을 조회합니다.", postListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    // 조회수 많은 팝업 목록 조회
    @GetMapping("/good/list")
    public ResponseEntity<?> famousPostList(){
        List<PostListDto> postListDtos = postMetricsService.famousPostList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post 목록을 조회합니다.", postListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내가 작성한 팝업 리스트
    @GetMapping("/my/list")
    public ResponseEntity<?> myPostList(Pageable pageable){
        Page<PostListDto> postListDtos = postService.myPostList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "나의 post 목록을 조회합니다.", postListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 포스트 상세 내역
    @GetMapping("/detail/{id}")
    public ResponseEntity<CommonResDto> getPostDetail(@PathVariable Long id){
        PostDetailDto postDetail = postService.getPostDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Post 상세정보를 조회합니다.", postDetail);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 조회수 증가 및 조회
    @GetMapping("/detail/views/{id}")
    public ResponseEntity<CommonResDto> getPostViews(@PathVariable Long id) {
        postMetricsService.incrementPostViews(id); // 조회수 증가
        Long views = postMetricsService.getPostViews(id); // 조회수 조회
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회수를 조회합니다.", views);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 좋아요 추가
    @PostMapping("/detail/like/{id}")
    public ResponseEntity<CommonResDto> likePost(@PathVariable Long id) {
        postMetricsService.likePost(id);
        Long like = postMetricsService.getPostLikesCount(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "좋아요가 추가되었습니다.", like);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 좋아요 취소
    @PostMapping("/detail/unlike/{id}")
    public ResponseEntity<CommonResDto> unlikePost(@PathVariable Long id) {
        postMetricsService.unlikePost(id);
        Long like = postMetricsService.getPostLikesCount(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "좋아요가 취소되었습니다.", like);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 좋아요 수 조회
    @GetMapping("/detail/{id}/likes")
    public ResponseEntity<CommonResDto> getPostLikesCount(@PathVariable Long id) {
        Long likesCount = postMetricsService.getPostLikesCount(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "좋아요 수를 조회합니다.", likesCount);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 검색 및 필터링 (페이징 지원)
    @PostMapping("/search")
    public ResponseEntity<CommonResDto> searchPosts(@RequestBody SearchFilterReqDto searchFilter, Pageable pageable) {
        Page<PostListDto> results = postService.searchAndFilter(searchFilter, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "검색 결과를 조회합니다.", results);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 검색 및 필터링 (전체 리스트)
    @PostMapping("/search/all")
    public ResponseEntity<CommonResDto> searchPostsAll(@RequestBody SearchFilterReqDto searchFilter) {
        List<PostListDto> results = postService.searchAndFilterList(searchFilter);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "검색 결과를 조회합니다.", results);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // === PostDetail (상세 영업 정보) API ===

    // 상세 영업 정보 생성
    @PostMapping("/detail/{postId}/business-info")
    public ResponseEntity<CommonResDto> createPostDetail(
            @PathVariable Long postId,
            @RequestBody PostDetailSaveDto dto) {
        PostDetailResDto postDetail = postDetailService.createPostDetail(postId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "상세 영업 정보가 생성되었습니다.", postDetail);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 상세 영업 정보 조회
    @GetMapping("/detail/{postId}/business-info")
    public ResponseEntity<CommonResDto> getPostDetailInfo(@PathVariable Long postId) {
        PostDetailResDto postDetail = postDetailService.getPostDetail(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "상세 영업 정보를 조회합니다.", postDetail);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 상세 영업 정보 수정
    @PutMapping("/detail/{postId}/business-info")
    public ResponseEntity<CommonResDto> updatePostDetail(
            @PathVariable Long postId,
            @RequestBody PostDetailUpdateDto dto) {
        PostDetailResDto postDetail = postDetailService.updatePostDetail(postId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "상세 영업 정보가 수정되었습니다.", postDetail);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 상세 영업 정보 삭제
    @DeleteMapping("/detail/{postId}/business-info")
    public ResponseEntity<CommonResDto> deletePostDetail(@PathVariable Long postId) {
        postDetailService.deletePostDetail(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "상세 영업 정보가 삭제되었습니다.", postId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
