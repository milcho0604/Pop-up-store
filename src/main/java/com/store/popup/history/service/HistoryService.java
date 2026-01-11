package com.store.popup.history.service;

import com.store.popup.common.service.DistributedLockService;
import com.store.popup.history.domain.SearchHistory;
import com.store.popup.history.domain.ViewHistory;
import com.store.popup.history.dto.SearchHistoryDto;
import com.store.popup.history.dto.ViewHistoryDto;
import com.store.popup.history.repository.SearchHistoryRepository;
import com.store.popup.history.repository.ViewHistoryRepository;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class HistoryService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final DistributedLockService distributedLockService;

    // ========== 조회 히스토리 ==========

    /**
     * Post 조회 기록 추가/갱신 (분산 락 + UPSERT)
     *
     * 동시성 제어 전략:
     * 1. Redis 분산 락: 여러 서버 인스턴스 간 동시 접근 방지
     * 2. Database UPSERT: DB 레벨에서 원자적 연산 보장
     *
     * 이중 안전장치로 Race Condition을 완벽히 차단합니다.
     */
    public void recordViewHistory(Long postId) {
        try {
            Member member = getCurrentMember();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

            // 분산 락 키: "view-history:member:{memberId}:post:{postId}"
            String lockKey = String.format("view-history:member:%d:post:%d", member.getId(), postId);

            // 분산 락을 획득하여 동시성 제어
            distributedLockService.executeWithLock(lockKey, () -> {
                // UPSERT 쿼리로 조회 기록 생성 또는 갱신
                // - 새로운 조회: INSERT
                // - 기존 조회: updated_at만 갱신
                viewHistoryRepository.upsertViewHistory(member.getId(), postId);
            }, 2000L, 3000L);  // 대기 2초, 자동 해제 3초

            log.debug("조회 기록 저장/갱신 성공: member={}, post={}", member.getId(), postId);

        } catch (EntityNotFoundException e) {
            // 익명 사용자는 조회 기록을 남기지 않음
            log.debug("조회 기록 저장 실패 (익명 사용자일 수 있음): {}", e.getMessage());
        } catch (IllegalStateException e) {
            // 분산 락 획득 실패 - 치명적이지 않으므로 로그만 남김
            log.warn("조회 기록 저장 시 락 획득 실패: postId={}, message={}", postId, e.getMessage());
        }
    }

    /**
     * 내 조회 히스토리 조회
     */
    @Transactional(readOnly = true)
    public List<ViewHistoryDto> getMyViewHistory() {
        Member member = getCurrentMember();
        List<ViewHistory> histories = viewHistoryRepository.findByMemberOrderByUpdatedAtDesc(member);
        return histories.stream()
                .map(ViewHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 조회 히스토리 삭제
     */
    public void deleteViewHistory(Long historyId) {
        Member member = getCurrentMember();
        ViewHistory viewHistory = viewHistoryRepository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 조회 기록입니다."));

        if (!viewHistory.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 조회 기록만 삭제할 수 있습니다.");
        }

        // soft delete
        viewHistory.updateDeleteAt();
    }

    /**
     * 조회 히스토리 전체 삭제
     */
    public void deleteAllViewHistory() {
        Member member = getCurrentMember();
        viewHistoryRepository.softDeleteAllByMember(member);
    }

    // ========== 검색 기록 ==========

    /**
     * 검색 기록 저장
     */
    public void recordSearchHistory(String keyword) {
        try {
            Member member = getCurrentMember();
            SearchHistory searchHistory = SearchHistory.builder()
                    .member(member)
                    .keyword(keyword)
                    .build();
            searchHistoryRepository.save(searchHistory);
        } catch (EntityNotFoundException e) {
            // 익명 사용자는 검색 기록을 남기지 않음
            log.debug("검색 기록 저장 실패 (익명 사용자일 수 있음): {}", e.getMessage());
        }
    }

    /**
     * 내 검색 기록 조회
     */
    @Transactional(readOnly = true)
    public List<SearchHistoryDto> getMySearchHistory() {
        Member member = getCurrentMember();
        List<SearchHistory> histories = searchHistoryRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);
        return histories.stream()
                .map(SearchHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 검색 기록 삭제
     */
    public void deleteSearchHistory(Long historyId) {
        Member member = getCurrentMember();
        SearchHistory searchHistory = searchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 검색 기록입니다."));

        if (!searchHistory.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 검색 기록만 삭제할 수 있습니다.");
        }

        // soft delete
        searchHistory.updateDeleteAt();
    }

    /**
     * 검색 기록 전체 삭제
     */
    public void deleteAllSearchHistory() {
        Member member = getCurrentMember();
        searchHistoryRepository.softDeleteAllByMember(member);
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
