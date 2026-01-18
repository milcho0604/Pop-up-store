package com.store.popup.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.domain.Type;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
@Transactional
public class FcmService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public void saveFcmToken(String memberEmail, FcmTokenSaveRequest dto) {
        Member member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        member.updateFcmToken(dto.getFcmToken());
    }

    // 알림 전송
    public void sendMessage(String memberEmail, String title, String body, Type type, Long id) {

        Member member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        String token = member.getFcmToken();

        // 리다이렉트 url 셋팅
        String urlType = null;
        String url = null;
        if (id == null) {
            if (member.getRole().equals(Role.ADMIN)) {
                if (type.equals(Type.REGISTER)) {
                    urlType = "admin/member/detail/" + id.toString();
                    url = "http://localhost:8081/" + urlType;
                } else if (type.equals(Type.REPORT_NOTIFICATION)) {
                    urlType = "admin/information/list/" + id.toString();
                    url = "http://localhost:8081/" + urlType;
                } else if (type.equals(Type.POST_NOTIFICATION)) {
                    urlType = "admin/information/detail/" + id.toString();
                    url = "http://localhost:8081/" + urlType;
                }
            }

            // 조립
            FcmNotification fcmNotification = FcmNotification.builder()
                    .member(member)
                    .title(title)
                    .content(body)  //알림 내용 저장
                    .isRead(false)      //fcmNotification 생성될때 = false -> 사용자가 알림 누르는 순간 true로 바껴야함
                    .type(type)
                    .refId(id)        //등록된 post의 Id
                    .recipient(memberEmail)
                    .url(url)
                    .build();

            Message message = Message.builder()
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .build())
                    .putData("url", url) //이동할 url 추가
                    .putData("notificationId", String.valueOf(fcmNotification.getId())) //이동할 url 추가
                    .setToken(token)
                    .build();
            System.out.println(message.toString());
            try {
                // 비동기 처리 결과 기다리기
                String response = FirebaseMessaging.getInstance().sendAsync(message).get();
                System.out.println("Successfully send message: " + response);
                // db에 FcmNotification 저장
                notificationRepository.save(fcmNotification);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 스레드 상태 복원
                e.printStackTrace();
                throw new RuntimeException("Thread was interrupted during FCM message sending: " + e.getMessage(), e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException("FCM 메시지 전송 중 오류 발생: " + e.getCause().getMessage(), e.getCause());
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error during FCM message sending: " + e.getMessage(), e);
            }
        }
    }

}
