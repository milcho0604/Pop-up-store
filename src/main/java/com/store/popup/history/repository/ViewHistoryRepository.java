package com.store.popup.history.repository;

import com.store.popup.history.domain.ViewHistory;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
