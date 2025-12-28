package com.store.popup.notice.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.notice.dto.NoticeResDto;
import com.store.popup.notice.dto.NoticeSaveDto;
import com.store.popup.notice.dto.NoticeUpdateDto;
import com.store.popup.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 관리자 전용 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notice/admin")
public class NoticeAdminController {

    private final NoticeService noticeService;

    // 공지사항 작성 (관리자만)
    @PostMapping("/create")
    public ResponseEntity<CommonResDto> createNotice(@RequestBody NoticeSaveDto dto) {
        NoticeResDto notice = noticeService.createNotice(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "공지사항이 작성되었습니다.", notice);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 공지사항 수정 (관리자만)
    @PutMapping("/{noticeId}")
    public ResponseEntity<CommonResDto> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateDto dto) {
        NoticeResDto notice = noticeService.updateNotice(noticeId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "공지사항이 수정되었습니다.", notice);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 공지사항 삭제 (관리자만)
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<CommonResDto> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "공지사항이 삭제되었습니다.", noticeId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 모든 공지사항 조회 (관리자용 - 게시기간 무관)
    @GetMapping("/list")
    public ResponseEntity<CommonResDto> getAllNotices(Pageable pageable) {
        Page<NoticeResDto> notices = noticeService.getAllNotices(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "전체 공지사항 목록을 조회합니다.", notices);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
