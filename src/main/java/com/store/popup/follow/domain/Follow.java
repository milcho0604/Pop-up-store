package com.store.popup.follow.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팔로우 엔티티 (유저 간 팔로우)
 */
@Entity
@Table(name = "follow", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Follow extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;  // 팔로우 하는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Member following;  // 팔로우 당하는 사람 (팔로잉)
}
