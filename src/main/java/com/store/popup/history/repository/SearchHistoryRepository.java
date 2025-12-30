package com.store.popup.history.repository;

import com.store.popup.history.domain.SearchHistory;
import com.store.popup.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // 회원의 검색 기록 (최신순)
    List<SearchHistory> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member);

    // 회원의 검색 기록 (페이징)
    Page<SearchHistory> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member, Pageable pageable);

    // 최근 인기 검색어 (전체 사용자 기준)
    List<SearchHistory> findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();
}
