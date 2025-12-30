package com.store.popup.follow.service;

import com.store.popup.follow.domain.Follow;
import com.store.popup.follow.dto.FollowDto;
import com.store.popup.follow.dto.FollowStatsDto;
import com.store.popup.follow.repository.FollowRepository;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 팔로우하기
     */
    public void follow(Long targetMemberId) {
        Member follower = getCurrentMember();
        Member following = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 자기 자신을 팔로우할 수 없음
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        // 이미 팔로우 중인지 확인
        if (followRepository.existsByFollowerAndFollowingAndDeletedAtIsNull(follower, following)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
        log.info("팔로우 성공: {} -> {}", follower.getNickname(), following.getNickname());
    }

    /**
     * 언팔로우하기
     */
    public void unfollow(Long targetMemberId) {
        Member follower = getCurrentMember();
        Member following = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Follow follow = followRepository.findByFollowerAndFollowingAndDeletedAtIsNull(follower, following)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 관계가 존재하지 않습니다."));

        // soft delete
        follow.updateDeleteAt();
        log.info("언팔로우 성공: {} -> {}", follower.getNickname(), following.getNickname());
    }

    /**
     * 팔로우 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long targetMemberId) {
        Member follower = getCurrentMember();
        Member following = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        return followRepository.existsByFollowerAndFollowingAndDeletedAtIsNull(follower, following);
    }

    /**
     * 내가 팔로우하는 사람들 (팔로잉 목록)
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowingList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        List<Follow> followList = followRepository.findFollowingList(member);
        return followList.stream()
                .map(FollowDto::fromFollowing)
                .collect(Collectors.toList());
    }

    /**
     * 나를 팔로우하는 사람들 (팔로워 목록)
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowerList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        List<Follow> followerList = followRepository.findFollowerList(member);
        return followerList.stream()
                .map(FollowDto::fromFollower)
                .collect(Collectors.toList());
    }

    /**
     * 팔로우 통계 조회
     */
    @Transactional(readOnly = true)
    public FollowStatsDto getFollowStats(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Long followerCount = followRepository.countByFollowingAndDeletedAtIsNull(member);
        Long followingCount = followRepository.countByFollowerAndDeletedAtIsNull(member);

        return FollowStatsDto.builder()
                .memberId(member.getId())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    /**
     * 내 팔로잉 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getMyFollowingList() {
        Member member = getCurrentMember();
        return getFollowingList(member.getId());
    }

    /**
     * 내 팔로워 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getMyFollowerList() {
        Member member = getCurrentMember();
        return getFollowerList(member.getId());
    }

    /**
     * 내 팔로우 통계 조회
     */
    @Transactional(readOnly = true)
    public FollowStatsDto getMyFollowStats() {
        Member member = getCurrentMember();
        return getFollowStats(member.getId());
    }

    // ========== Helper Methods ==========

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
