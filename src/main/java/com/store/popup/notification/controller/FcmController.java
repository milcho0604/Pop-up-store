package com.store.popup.notification.controller;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.domain.Type;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.dto.NotificationResDto;
import com.store.popup.notification.service.FcmService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    // 토큰 발급 및 저장
    @PostMapping("/token")
    public ResponseEntity<?> saveFcmToken(@RequestBody @Valid FcmTokenSaveRequest dto, Authentication authentication) {
        // 이메일과 FCM 토큰 정보를 사용하여 저장 로직 호출
        fcmService.saveFcmToken(dto);
        // 성공 응답 객체 생성
        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "FCM 토큰이 성공적으로 저장되었습니다.", null);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // TODO : 로직 분리 고민
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody String memberEmail, @RequestBody String title, String body, Type type, Long id){
        fcmService.sendMessage(title, body, type, id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "fcm 메세지 전송 성공", title + ": " + type);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // fcm 토큰 삭제
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> memberEmailMap) {
        // TODO: 수정힐까..? 토큰만 받아서 처리하기로..?
        String memberEmail = memberEmailMap.get("memberEmail");
        fcmService.logout();
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "fcm 토큰 삭제가 성공적으로 처리되었습니다.", memberEmail), HttpStatus.OK);
    }
}
