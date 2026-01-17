package com.store.popup.notification.service;

import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void saveFcmToken(String memberEmail, FcmTokenSaveRequest dto) {
        Member member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        member.updateFcmToken(dto.getFcmToken());
    }

}
