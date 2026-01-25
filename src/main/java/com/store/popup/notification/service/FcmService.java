package com.store.popup.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.dto.SendFcmReqDto;
import com.store.popup.notification.exception.FcmException;
import com.store.popup.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FcmService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    // FCM 토큰 저장
    public void saveFcmToken(FcmTokenSaveRequest dto) {
        Member member = getCurrentMember();
        member.updateFcmToken(dto.getFcmToken());
    }

    // 알림 발송(DB save)
    public void sendMessage(SendFcmReqDto dto) {
        Member member = memberRepository.findByMemberEmailOrThrow(dto.getMemberEmail());

        FcmNotification notification = FcmNotification.create(member, dto.getTitle(), dto.getContent(), dto.getType(), dto.getRefId());
        notificationRepository.save(notification);

        if (member.getFcmToken() == null || member.getFcmToken().isBlank()) {
            log.warn("FCM 토큰이 없어 푸시 알림을 전송하지 않습니다. memberEmail: {}", member.getMemberEmail());
            return;
        }
        sendFcmMessage(notification, member.getFcmToken());
    }

    // fcm으로 메시지 발송
    private void sendFcmMessage(FcmNotification notification, String token) {
//        Message message = Message.builder()
//                .setWebpushConfig(WebpushConfig.builder()
//                        .setNotification(WebpushNotification.builder()
//                                .setTitle(notification.getTitle())
//                                .setBody(notification.getContent())
//                                .build())
//                        .build())
//                .putData("url", notification.getUrl()) //이동할 url 추가
//                .putData("notificationId", String.valueOf(notification.getId()))
//                .setToken(token)
//                .build();
        Message message = notification.toFcmMessage(token);
        log.debug("Sending FCM message - title: {}, content: {}, token: {}...",
                notification.getTitle(), notification.getContent(), token.substring(0, Math.min(20, token.length())));

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Successfully sent FCM message: {}", response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("FCM 메시지 전송 중 인터럽트 발생", e);
            throw FcmException.sendInterrupted();
        } catch (ExecutionException e) {
            log.error("FCM 메시지 전송 실패: {}", e.getCause().getMessage(), e.getCause());
            throw FcmException.sendFailed();
        }
    }

    // fcm 토큰 삭제
    public void logout() {
        // 회원 조회
        Member member = getCurrentMember();
        // FCM 토큰 초기화
        member.setFcmToken();
        memberRepository.save(member);
    }

    // 공통 메서드
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
    }
}
