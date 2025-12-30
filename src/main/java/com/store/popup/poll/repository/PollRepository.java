package com.store.popup.poll.repository;

import com.store.popup.poll.domain.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    // 모든 투표 조회 (관리자용)
    Page<Poll> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    // 현재 진행중인 투표 조회 (페이징)
    @Query("SELECT p FROM Poll p WHERE p.deletedAt IS NULL " +
           "AND p.startDate <= :now AND p.endDate >= :now " +
           "ORDER BY p.createdAt DESC")
    Page<Poll> findActivePolls(@Param("now") LocalDateTime now, Pageable pageable);

    // 현재 진행중인 투표 조회 (리스트)
    @Query("SELECT p FROM Poll p WHERE p.deletedAt IS NULL " +
           "AND p.startDate <= :now AND p.endDate >= :now " +
           "ORDER BY p.createdAt DESC")
    List<Poll> findActivePollsList(@Param("now") LocalDateTime now);

    // Poll을 options와 함께 fetch join
    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.options WHERE p.id = :pollId AND p.deletedAt IS NULL")
    Optional<Poll> findByIdWithOptions(@Param("pollId") Long pollId);
}
