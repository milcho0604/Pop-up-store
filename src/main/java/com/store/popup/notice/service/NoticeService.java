package com.store.popup.notice.service;

import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.notice.domain.Notice;
import com.store.popup.notice.dto.NoticeResDto;
import com.store.popup.notice.dto.NoticeSaveDto;
import com.store.popup.notice.dto.NoticeUpdateDto;
import com.store.popup.notice.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    // ========== 관리자 전용 기능 ==========

    // 공지사항 작성 (관리자만)
    public NoticeResDto createNotice(NoticeSaveDto dto) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        // 게시 종료일이 시작일보다 빠르면 에러
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("게시 종료일은 시작일보다 늦어야 합니다.");
        }

        Notice notice = dto.toEntity(admin);
        Notice savedNotice = noticeRepository.save(notice);

        return NoticeResDto.fromEntity(savedNotice);
    }

    // 공지사항 수정 (관리자만)
    public NoticeResDto updateNotice(Long noticeId, NoticeUpdateDto dto) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));

        // 게시 종료일이 시작일보다 빠르면 에러
        LocalDateTime newStartDate = dto.getStartDate() != null ? dto.getStartDate() : notice.getStartDate();
        LocalDateTime newEndDate = dto.getEndDate() != null ? dto.getEndDate() : notice.getEndDate();
        if (newEndDate.isBefore(newStartDate)) {
            throw new IllegalArgumentException("게시 종료일은 시작일보다 늦어야 합니다.");
        }

        // dirty checking
        notice.update(dto.getTitle(), dto.getContent(), dto.getNoticeType(),
                dto.getStartDate(), dto.getEndDate());

        return NoticeResDto.fromEntity(notice);
    }

    // 공지사항 삭제 (관리자만)
    public void deleteNotice(Long noticeId) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));

        // soft delete
        notice.updateDeleteAt();
    }

    // 모든 공지사항 조회 (관리자용 - 게시기간 무관)
    @Transactional(readOnly = true)
    public Page<NoticeResDto> getAllNotices(Pageable pageable) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Page<Notice> notices = noticeRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
        return notices.map(NoticeResDto::fromEntity);
    }

    // ========== 공개 API (누구나 조회 가능) ==========

    // 현재 게시중인 공지사항 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<NoticeResDto> getActiveNotices(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findActiveNotices(LocalDateTime.now(), pageable);
        return notices.map(NoticeResDto::fromEntity);
    }

    // 현재 게시중인 공지사항 조회 (리스트)
    @Transactional(readOnly = true)
    public List<NoticeResDto> getActiveNoticesList() {
        List<Notice> notices = noticeRepository.findActiveNoticesList(LocalDateTime.now());
        return notices.stream()
                .map(NoticeResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 현재 게시중인 팝업 공지사항 조회
    @Transactional(readOnly = true)
    public List<NoticeResDto> getActivePopupNotices() {
        List<Notice> notices = noticeRepository.findActivePopupNotices(LocalDateTime.now());
        return notices.stream()
                .map(NoticeResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 타입별 공지사항 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<NoticeResDto> getActiveNoticesByType(NoticeType noticeType, Pageable pageable) {
        Page<Notice> notices = noticeRepository.findActiveNoticesByType(
                noticeType, LocalDateTime.now(), pageable);
        return notices.map(NoticeResDto::fromEntity);
    }

    // 타입별 공지사항 조회 (리스트)
    @Transactional(readOnly = true)
    public List<NoticeResDto> getActiveNoticesByTypeList(NoticeType noticeType) {
        List<Notice> notices = noticeRepository.findActiveNoticesByTypeList(
                noticeType, LocalDateTime.now());
        return notices.stream()
                .map(NoticeResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 공지사항 상세 조회 (조회수 증가)
    public NoticeResDto getNoticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));

        if (notice.getDeletedAt() != null) {
            throw new EntityNotFoundException("삭제된 공지사항입니다.");
        }

        // 조회수 증가 (dirty checking)
        notice.incrementViewCount();

        return NoticeResDto.fromEntity(notice);
    }

    // ========== Helper Methods ==========

    // 관리자 권한 확인
    private void checkAdminRole(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }

    // 현재 로그인한 회원 조회
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
