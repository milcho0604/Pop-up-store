package com.store.popup.history.repository;

import com.store.popup.history.domain.ViewHistory;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    // 회원의 조회 히스토리 (최신순)
    @Query("SELECT vh FROM ViewHistory vh JOIN FETCH vh.post WHERE vh.member = :member AND vh.deletedAt IS NULL ORDER BY vh.updatedAt DESC")
    List<ViewHistory> findByMemberOrderByUpdatedAtDesc(@Param("member") Member member);

    // 회원의 조회 히스토리 (페이징)
    Page<ViewHistory> findByMemberAndDeletedAtIsNullOrderByUpdatedAtDesc(Member member, Pageable pageable);

    // 특정 Post 조회 기록 확인
    Optional<ViewHistory> findByMemberAndPostAndDeletedAtIsNull(Member member, Post post);

    // 회원의 모든 조회 히스토리 일괄 삭제 (벌크 업데이트)
    @Modifying
    @Query("UPDATE ViewHistory vh SET vh.deletedAt = CURRENT_TIMESTAMP WHERE vh.member = :member AND vh.deletedAt IS NULL")
    void softDeleteAllByMember(@Param("member") Member member);

    /**
     * 조회 기록 생성 또는 갱신 (UPSERT)
     *
     * MariaDB의 INSERT ... ON DUPLICATE KEY UPDATE 구문을 사용하여
     * 동시성 문제를 데이터베이스 레벨에서 해결합니다.
     *
     * - 새로운 조회: INSERT 실행
     * - 기존 조회: updated_at만 갱신
     * - 원자적 연산으로 Race Condition 방지
     *
     * @param memberId 회원 ID
     * @param postId 게시글 ID
     */
    @Modifying
    @Query(value = "INSERT INTO view_history (member_id, post_id, created_at, updated_at, deleted_at) " +
                   "VALUES (:memberId, :postId, NOW(), NOW(), NULL) " +
                   "ON DUPLICATE KEY UPDATE updated_at = NOW()",
           nativeQuery = true)
    void upsertViewHistory(@Param("memberId") Long memberId, @Param("postId") Long postId);
}
