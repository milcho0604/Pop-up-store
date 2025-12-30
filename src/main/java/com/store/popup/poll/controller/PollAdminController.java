package com.store.popup.poll.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.poll.dto.PollOptionSaveDto;
import com.store.popup.poll.dto.PollResDto;
import com.store.popup.poll.dto.PollSaveDto;
import com.store.popup.poll.dto.PollUpdateDto;
import com.store.popup.poll.service.PollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 투표 관리자 전용 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/poll/admin")
public class PollAdminController {

    private final PollService pollService;

    // 투표 생성 (관리자만)
    @PostMapping("/create")
    public ResponseEntity<CommonResDto> createPoll(@RequestBody PollSaveDto dto) {
        PollResDto poll = pollService.createPoll(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "투표가 생성되었습니다.", poll);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 투표에 선택지 추가 (관리자만)
    @PostMapping("/{pollId}/option")
    public ResponseEntity<CommonResDto> addPollOption(
            @PathVariable Long pollId,
            @RequestBody PollOptionSaveDto dto) {
        PollResDto poll = pollService.addPollOption(pollId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "선택지가 추가되었습니다.", poll);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 투표 수정 (관리자만)
    @PutMapping("/{pollId}")
    public ResponseEntity<CommonResDto> updatePoll(
            @PathVariable Long pollId,
            @RequestBody PollUpdateDto dto) {
        PollResDto poll = pollService.updatePoll(pollId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "투표가 수정되었습니다.", poll);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 투표 삭제 (관리자만)
    @DeleteMapping("/{pollId}")
    public ResponseEntity<CommonResDto> deletePoll(@PathVariable Long pollId) {
        pollService.deletePoll(pollId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "투표가 삭제되었습니다.", pollId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 선택지 삭제 (관리자만)
    @DeleteMapping("/option/{optionId}")
    public ResponseEntity<CommonResDto> deletePollOption(@PathVariable Long optionId) {
        pollService.deletePollOption(optionId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "선택지가 삭제되었습니다.", optionId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 모든 투표 조회 (관리자용)
    @GetMapping("/list")
    public ResponseEntity<CommonResDto> getAllPolls(Pageable pageable) {
        Page<PollResDto> polls = pollService.getAllPolls(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "전체 투표 목록을 조회합니다.", polls);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
