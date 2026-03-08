package com.store.popup.notification.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notification.dto.FcmTokenSaveRequest;
import com.store.popup.notification.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fcm")
@PreAuthorize("isAuthenticated()")
public class FcmController {

    private final FcmService fcmService;

    // 토큰 발급 및 저장
    @PostMapping("/token")
    public ResponseEntity<?> saveFcmToken(@RequestBody @Valid FcmTokenSaveRequest dto) {
        fcmService.saveFcmToken(dto);
        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "FCM 토큰이 성공적으로 저장되었습니다.", null);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // fcm 토큰 삭제
    @DeleteMapping("/token")
    public ResponseEntity<?> logout() {
        fcmService.logout();
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "fcm 토큰 삭제가 성공적으로 처리되었습니다.", null), HttpStatus.OK);
    }
}
