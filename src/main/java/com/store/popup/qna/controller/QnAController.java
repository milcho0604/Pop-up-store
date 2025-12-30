package com.store.popup.qna.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.qna.dto.AnswerSaveDto;
import com.store.popup.qna.dto.QuestionDto;
import com.store.popup.qna.dto.QuestionSaveDto;
import com.store.popup.qna.service.QnAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Q&A 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post/{postId}/qna")
public class QnAController {

    private final QnAService qnaService;

    // ========== 질문 관련 ==========

    /**
     * 질문 작성
     */
    @PostMapping("/question")
    public ResponseEntity<CommonResDto> createQuestion(
            @PathVariable Long postId,
            @RequestBody QuestionSaveDto dto) {
        QuestionDto question = qnaService.createQuestion(postId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "질문이 작성되었습니다.", question);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    /**
     * Post의 모든 질문 조회 (페이징)
     */
    @GetMapping("/question")
    public ResponseEntity<CommonResDto> getQuestions(
            @PathVariable Long postId,
            Pageable pageable) {
        Page<QuestionDto> questions = qnaService.getQuestions(postId, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "질문 목록을 조회합니다.", questions);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * Post의 모든 질문 조회 (리스트)
     */
    @GetMapping("/question/all")
    public ResponseEntity<CommonResDto> getQuestionsList(@PathVariable Long postId) {
        List<QuestionDto> questions = qnaService.getQuestionsList(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "전체 질문 목록을 조회합니다.", questions);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 질문 상세 조회 (답변 포함)
     */
    @GetMapping("/question/{questionId}")
    public ResponseEntity<CommonResDto> getQuestionDetail(
            @PathVariable Long postId,
            @PathVariable Long questionId) {
        QuestionDto question = qnaService.getQuestionDetail(questionId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "질문 상세 정보를 조회합니다.", question);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 질문 삭제
     */
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<CommonResDto> deleteQuestion(
            @PathVariable Long postId,
            @PathVariable Long questionId) {
        qnaService.deleteQuestion(questionId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "질문이 삭제되었습니다.", questionId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // ========== 답변 관련 ==========

    /**
     * 답변 작성 (게시글 작성자 또는 관리자만)
     */
    @PostMapping("/question/{questionId}/answer")
    public ResponseEntity<CommonResDto> createAnswer(
            @PathVariable Long postId,
            @PathVariable Long questionId,
            @RequestBody AnswerSaveDto dto) {
        QuestionDto question = qnaService.createAnswer(questionId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "답변이 작성되었습니다.", question);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    /**
     * 답변 수정 (답변 작성자만)
     */
    @PutMapping("/answer/{answerId}")
    public ResponseEntity<CommonResDto> updateAnswer(
            @PathVariable Long postId,
            @PathVariable Long answerId,
            @RequestBody AnswerSaveDto dto) {
        QuestionDto question = qnaService.updateAnswer(answerId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "답변이 수정되었습니다.", question);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 답변 삭제 (답변 작성자만)
     */
    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<CommonResDto> deleteAnswer(
            @PathVariable Long postId,
            @PathVariable Long answerId) {
        qnaService.deleteAnswer(answerId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "답변이 삭제되었습니다.", answerId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
