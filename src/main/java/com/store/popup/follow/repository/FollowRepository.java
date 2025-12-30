package com.store.popup.follow.repository;

import com.store.popup.follow.domain.Follow;
import com.store.popup.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 여부 확인
    boolean existsByFollowerAndFollowingAndDeletedAtIsNull(Member follower, Member following);

    // 팔로우 관계 조회
    Optional<Follow> findByFollowerAndFollowingAndDeletedAtIsNull(Member follower, Member following);

    // 내가 팔로우하는 사람들 (팔로잉 목록)
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower = :follower AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Follow> findFollowingList(@Param("follower") Member follower);

    // 나를 팔로우하는 사람들 (팔로워 목록)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following = :following AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Follow> findFollowerList(@Param("following") Member following);

    // 팔로잉 수 (내가 팔로우하는 사람 수)
    Long countByFollowerAndDeletedAtIsNull(Member follower);

    // 팔로워 수 (나를 팔로우하는 사람 수)
    Long countByFollowingAndDeletedAtIsNull(Member following);
}
