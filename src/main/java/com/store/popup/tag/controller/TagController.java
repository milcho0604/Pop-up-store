package com.store.popup.tag.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.tag.dto.TagDto;
import com.store.popup.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    /**
     * 모든 태그 목록 조회
     * GET /tag/list
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllTags() {
        List<TagDto> tags = tagService.getAllTags();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "태그 목록을 조회합니다.", tags);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 인기 태그 목록 조회 (사용 횟수가 많은 순)
     * GET /tag/popular?limit=10
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        List<TagDto> tags = tagService.getPopularTags(limit);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "인기 태그 목록을 조회합니다.", tags);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
