package com.store.popup.notification.service;

import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.domain.Type;
import com.store.popup.notification.dto.NotificationResDto;
import com.store.popup.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    // 알림 리스트 조회
    public Page<NotificationResDto> myNotiList(Pageable pageable) {
        Member member = getCurrentMember();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Page<FcmNotification> notifications = notificationRepository.findByMemberIdAndCreatedAtAfter(member.getId(), sevenDaysAgo, pageable);
        return notifications.map(FcmNotification::toResponseDto);
    }

    // 단건 읽음 처리
    public FcmNotification read(Long id) {
        FcmNotification fcmNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("알림이 존재하지 않습니다."));
        fcmNotification.read();
        notificationRepository.save(fcmNotification);
        return fcmNotification;
    }

    // 선택한 알림 읽음 처리
    public int readByIds(List<Long> ids) {
        Member member = getCurrentMember();
        return notificationRepository.markAsReadByIds(ids, member.getId());
    }

    // 전체 읽음 처리
    public int readAll() {
        Member member = getCurrentMember();
        return notificationRepository.markAllAsRead(member.getId());
    }

    // 공통 메서드
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
    }
}
