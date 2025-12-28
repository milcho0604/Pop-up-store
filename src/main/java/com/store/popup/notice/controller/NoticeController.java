package com.store.popup.notice.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.notice.dto.NoticeResDto;
import com.store.popup.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공지사항 공개 컨트롤러 (누구나 조회 가능)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 상세 조회 (조회수 증가)
    @GetMapping("/{noticeId}")
    public ResponseEntity<CommonResDto> getNoticeDetail(@PathVariable Long noticeId) {
        NoticeResDto notice = noticeService.getNoticeDetail(noticeId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "공지사항을 조회합니다.", notice);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 현재 게시중인 공지사항 조회 (페이징)
    @GetMapping("/active")
    public ResponseEntity<CommonResDto> getActiveNotices(Pageable pageable) {
        Page<NoticeResDto> notices = noticeService.getActiveNotices(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "현재 게시중인 공지사항 목록을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 현재 게시중인 공지사항 조회 (전체 리스트)
    @GetMapping("/active/all")
    public ResponseEntity<CommonResDto> getActiveNoticesList() {
        List<NoticeResDto> notices = noticeService.getActiveNoticesList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "현재 게시중인 전체 공지사항 목록을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 현재 게시중인 팝업 공지사항 조회
    @GetMapping("/popup")
    public ResponseEntity<CommonResDto> getActivePopupNotices() {
        List<NoticeResDto> notices = noticeService.getActivePopupNotices();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팝업 공지사항을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 타입별 공지사항 조회 (페이징)
    @GetMapping("/type/{noticeType}")
    public ResponseEntity<CommonResDto> getActiveNoticesByType(
            @PathVariable NoticeType noticeType,
            Pageable pageable) {
        Page<NoticeResDto> notices = noticeService.getActiveNoticesByType(noticeType, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, noticeType + " 타입 공지사항을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 타입별 공지사항 조회 (전체 리스트)
    @GetMapping("/type/{noticeType}/all")
    public ResponseEntity<CommonResDto> getActiveNoticesByTypeList(@PathVariable NoticeType noticeType) {
        List<NoticeResDto> notices = noticeService.getActiveNoticesByTypeList(noticeType);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, noticeType + " 타입 전체 공지사항을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
