package com.store.popup.share.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.share.dto.ShareDto;
import com.store.popup.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공유 기능 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post/{postId}/share")
public class ShareController {

    private final ShareService shareService;

    /**
     * 공유 정보 조회 (URL, 메타데이터)
     */
    @GetMapping
    public ResponseEntity<CommonResDto> getShareInfo(@PathVariable Long postId) {
        ShareDto shareDto = shareService.getShareInfo(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "공유 정보를 조회합니다.", shareDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 공유 횟수 증가 (실제 공유 발생 시 호출)
     */
    @PostMapping
    public ResponseEntity<CommonResDto> incrementShareCount(@PathVariable Long postId) {
        shareService.incrementShareCount(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "공유 횟수가 증가되었습니다.", postId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
