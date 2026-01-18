package com.store.popup.notification.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.domain.Type;
import com.store.popup.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotificationController {
    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody String memberEmail, @RequestBody String title, String body, Type type, Long id){
        fcmService.sendMessage(memberEmail, title, body, type, id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "fcm 메세지 전송 성공", title + ": " + type);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
