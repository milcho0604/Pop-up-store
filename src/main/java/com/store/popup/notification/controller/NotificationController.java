package com.store.popup.notification.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notification.domain.FcmNotification;
import com.store.popup.notification.domain.Type;
import com.store.popup.notification.dto.NotificationResDto;
import com.store.popup.notification.service.FcmService;
import com.store.popup.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotificationController {
    private final NotificationService notificationService;
    // 알림 리스트
    @GetMapping("/list")
    public ResponseEntity<CommonResDto> myNotis(@PageableDefault(size = 10) Pageable pageable) {
        Page<NotificationResDto> notificationResList = notificationService.myNotiList(pageable);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"알림 조회 성공",notificationResList),HttpStatus.OK);
    }

    // 단건 읽음 처리
    @PatchMapping("/read/{id}")
    public ResponseEntity<CommonResDto> read(@PathVariable Long id) {
        FcmNotification fcmNotification = notificationService.read(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "알림 읽음 처리 성공", fcmNotification), HttpStatus.OK);
    }

    // 선택한 알림 읽음 처리
    @PatchMapping("/read")
    public ResponseEntity<CommonResDto> readByIds(@RequestBody List<Long> ids) {
        int count = notificationService.readByIds(ids);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "알림 " + count + "건 읽음 처리 성공", count), HttpStatus.OK);
    }

    // 전체 읽음 처리
    @PatchMapping("/read/all")
    public ResponseEntity<CommonResDto> readAll() {
        int count = notificationService.readAll();
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "알림 " + count + "건 읽음 처리 성공", count), HttpStatus.OK);
    }
}
