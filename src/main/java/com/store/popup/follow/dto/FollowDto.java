package com.store.popup.follow.dto;

import com.store.popup.follow.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팔로우 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowDto {

    private Long followId;
    private Long memberId;
    private String memberEmail;
    private String memberNickname;
    private String profileImgUrl;
    private LocalDateTime followedAt;

    /**
     * 팔로잉 목록용 (내가 팔로우하는 사람)
     */
    public static FollowDto fromFollowing(Follow follow) {
        return FollowDto.builder()
                .followId(follow.getId())
                .memberId(follow.getFollowing().getId())
                .memberEmail(follow.getFollowing().getMemberEmail())
                .memberNickname(follow.getFollowing().getNickname())
                .profileImgUrl(follow.getFollowing().getProfileImgUrl())
                .followedAt(follow.getCreatedAt())
                .build();
    }

    /**
     * 팔로워 목록용 (나를 팔로우하는 사람)
     */
    public static FollowDto fromFollower(Follow follow) {
        return FollowDto.builder()
                .followId(follow.getId())
                .memberId(follow.getFollower().getId())
                .memberEmail(follow.getFollower().getMemberEmail())
                .memberNickname(follow.getFollower().getNickname())
                .profileImgUrl(follow.getFollower().getProfileImgUrl())
                .followedAt(follow.getCreatedAt())
                .build();
    }
}
