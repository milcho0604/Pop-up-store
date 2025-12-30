package com.store.popup.poll.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.poll.dto.PollResDto;
import com.store.popup.poll.dto.VoteReqDto;
import com.store.popup.poll.service.PollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 투표 공개 컨트롤러 (누구나 조회 및 참여 가능)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/poll")
public class PollController {

    private final PollService pollService;

    // 현재 진행중인 투표 조회 (페이징)
    @GetMapping("/active")
    public ResponseEntity<CommonResDto> getActivePolls(Pageable pageable) {
        Page<PollResDto> polls = pollService.getActivePolls(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "현재 진행중인 투표 목록을 조회합니다.", polls);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 현재 진행중인 투표 조회 (전체 리스트)
    @GetMapping("/active/all")
    public ResponseEntity<CommonResDto> getActivePollsList() {
        List<PollResDto> polls = pollService.getActivePollsList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "현재 진행중인 전체 투표 목록을 조회합니다.", polls);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 투표 상세 조회 (결과 포함)
    @GetMapping("/{pollId}")
    public ResponseEntity<CommonResDto> getPollDetail(@PathVariable Long pollId) {
        PollResDto poll = pollService.getPollDetail(pollId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "투표 상세 정보를 조회합니다.", poll);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 투표하기
    @PostMapping("/{pollId}/vote")
    public ResponseEntity<CommonResDto> vote(
            @PathVariable Long pollId,
            @RequestBody VoteReqDto dto) {
        PollResDto poll = pollService.vote(pollId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "투표가 완료되었습니다.", poll);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내가 참여한 투표 목록
    @GetMapping("/my/votes")
    public ResponseEntity<CommonResDto> getMyVotes() {
        List<PollResDto> polls = pollService.getMyVotes();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내가 참여한 투표 목록을 조회합니다.", polls);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
