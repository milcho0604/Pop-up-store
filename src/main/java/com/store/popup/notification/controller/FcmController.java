package com.store.popup.notification.controller;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.service.FcmService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<?> saveFcmToken(@RequestBody @Valid FcmTokenSaveRequest dto, Authentication authentication) {
            // 인증된 사용자 정보에서 이메일 추출
            String memberEmail = authentication.getName();  // authentication에서 이메일 추출
            // 이메일과 FCM 토큰 정보를 사용하여 저장 로직 호출
            fcmService.saveFcmToken(memberEmail, dto);
            // 성공 응답 객체 생성
            CommonResDto resDto = new CommonResDto(HttpStatus.OK, "FCM 토큰이 성공적으로 저장되었습니다.", null);
            return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

}
