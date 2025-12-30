package com.store.popup.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 팔로우 통계 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowStatsDto {

    private Long memberId;
    private Long followerCount;   // 팔로워 수 (나를 팔로우하는 사람)
    private Long followingCount;  // 팔로잉 수 (내가 팔로우하는 사람)
}
