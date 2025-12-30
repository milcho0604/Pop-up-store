package com.store.popup.history.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.history.dto.SearchHistoryDto;
import com.store.popup.history.dto.ViewHistoryDto;
import com.store.popup.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 활동 히스토리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    // ========== 조회 히스토리 ==========

    /**
     * 내 조회 히스토리 조회
     */
    @GetMapping("/view")
    public ResponseEntity<CommonResDto> getMyViewHistory() {
        List<ViewHistoryDto> histories = historyService.getMyViewHistory();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 히스토리를 조회합니다.", histories);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 조회 히스토리 삭제
     */
    @DeleteMapping("/view/{historyId}")
    public ResponseEntity<CommonResDto> deleteViewHistory(@PathVariable Long historyId) {
        historyService.deleteViewHistory(historyId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 기록이 삭제되었습니다.", historyId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 조회 히스토리 전체 삭제
     */
    @DeleteMapping("/view/all")
    public ResponseEntity<CommonResDto> deleteAllViewHistory() {
        historyService.deleteAllViewHistory();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "모든 조회 기록이 삭제되었습니다.", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // ========== 검색 기록 ==========

    /**
     * 내 검색 기록 조회
     */
    @GetMapping("/search")
    public ResponseEntity<CommonResDto> getMySearchHistory() {
        List<SearchHistoryDto> histories = historyService.getMySearchHistory();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "검색 기록을 조회합니다.", histories);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 검색 기록 삭제
     */
    @DeleteMapping("/search/{historyId}")
    public ResponseEntity<CommonResDto> deleteSearchHistory(@PathVariable Long historyId) {
        historyService.deleteSearchHistory(historyId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "검색 기록이 삭제되었습니다.", historyId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 검색 기록 전체 삭제
     */
    @DeleteMapping("/search/all")
    public ResponseEntity<CommonResDto> deleteAllSearchHistory() {
        historyService.deleteAllSearchHistory();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "모든 검색 기록이 삭제되었습니다.", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
