package com.store.popup.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisEmailService {
    // 레디스 이메일 인증
    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender emailSender;

    public void saveVerificationCode(String memberEmail, String code) {
        if (memberEmail == null || code == null) {
            throw new IllegalArgumentException("Email and code must not be null");
        }
        redisTemplate.opsForValue().set(memberEmail, code, 10, TimeUnit.MINUTES);
        System.out.println("Redis에 저장된 코드: " + code);  // 인증 코드 디버깅 출력, 시간 제한
    }

    public boolean verifyCode(String memberEmail, String code) {
        if (memberEmail == null || code == null) {
            throw new IllegalArgumentException("Email and code must not be null");
        }
        Object cachedCode = redisTemplate.opsForValue().get(memberEmail);
        System.out.println("Redis에서 가져온 코드: " + cachedCode);  // 저장된 코드 디버깅 출력
        if (cachedCode != null && cachedCode.equals(code)) {
            redisTemplate.delete(memberEmail);  // 검증 후 코드 삭제
            return true;
        }
        return false;
    }

    // 이메일 인증번호 전송
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to {}", to, e);
            throw e; // 상위에서 처리 or 도메인 예외로 변환
        }
    }

}
