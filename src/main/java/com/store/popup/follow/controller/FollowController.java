package com.store.popup.follow.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.follow.dto.FollowDto;
import com.store.popup.follow.dto.FollowStatsDto;
import com.store.popup.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로우/팔로워 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우하기
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<CommonResDto> follow(@PathVariable Long memberId) {
        followService.follow(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팔로우하였습니다.", memberId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 언팔로우하기
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommonResDto> unfollow(@PathVariable Long memberId) {
        followService.unfollow(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "언팔로우하였습니다.", memberId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 팔로우 여부 확인
     */
    @GetMapping("/{memberId}/check")
    public ResponseEntity<CommonResDto> isFollowing(@PathVariable Long memberId) {
        boolean isFollowing = followService.isFollowing(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팔로우 여부를 조회합니다.", isFollowing);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 특정 회원의 팔로잉 목록 조회
     */
    @GetMapping("/{memberId}/following")
    public ResponseEntity<CommonResDto> getFollowingList(@PathVariable Long memberId) {
        List<FollowDto> followingList = followService.getFollowingList(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팔로잉 목록을 조회합니다.", followingList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 특정 회원의 팔로워 목록 조회
     */
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<CommonResDto> getFollowerList(@PathVariable Long memberId) {
        List<FollowDto> followerList = followService.getFollowerList(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팔로워 목록을 조회합니다.", followerList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 특정 회원의 팔로우 통계 조회
     */
    @GetMapping("/{memberId}/stats")
    public ResponseEntity<CommonResDto> getFollowStats(@PathVariable Long memberId) {
        FollowStatsDto stats = followService.getFollowStats(memberId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팔로우 통계를 조회합니다.", stats);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 내 팔로잉 목록 조회
     */
    @GetMapping("/my/following")
    public ResponseEntity<CommonResDto> getMyFollowingList() {
        List<FollowDto> followingList = followService.getMyFollowingList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 팔로잉 목록을 조회합니다.", followingList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 내 팔로워 목록 조회
     */
    @GetMapping("/my/followers")
    public ResponseEntity<CommonResDto> getMyFollowerList() {
        List<FollowDto> followerList = followService.getMyFollowerList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 팔로워 목록을 조회합니다.", followerList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 내 팔로우 통계 조회
     */
    @GetMapping("/my/stats")
    public ResponseEntity<CommonResDto> getMyFollowStats() {
        FollowStatsDto stats = followService.getMyFollowStats();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 팔로우 통계를 조회합니다.", stats);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
