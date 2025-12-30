package com.store.popup.history.service;

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

    // ========== 조회 히스토리 ==========

    /**
     * Post 조회 기록 추가/갱신
     */
    public void recordViewHistory(Long postId) {
        try {
            Member member = getCurrentMember();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

            // 이미 조회한 적이 있으면 updatedAt만 갱신
            ViewHistory viewHistory = viewHistoryRepository.findByMemberAndPostAndDeletedAtIsNull(member, post)
                    .orElse(null);

            if (viewHistory != null) {
                viewHistory.refreshViewTime();
            } else {
                // 새로운 조회 기록 생성
                viewHistory = ViewHistory.builder()
                        .member(member)
                        .post(post)
                        .build();
                viewHistoryRepository.save(viewHistory);
            }
        } catch (Exception e) {
            // 익명 사용자는 조회 기록을 남기지 않음
            log.debug("조회 기록 저장 실패 (익명 사용자일 수 있음): {}", e.getMessage());
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
        List<ViewHistory> histories = viewHistoryRepository.findByMemberOrderByUpdatedAtDesc(member);
        histories.forEach(ViewHistory::updateDeleteAt);
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
        } catch (Exception e) {
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
        List<SearchHistory> histories = searchHistoryRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);
        histories.forEach(SearchHistory::updateDeleteAt);
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
