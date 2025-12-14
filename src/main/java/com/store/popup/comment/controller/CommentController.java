package com.store.popup.comment.controller;

import com.store.popup.comment.domain.Comment;
import com.store.popup.comment.dto.CommentDetailDto;
import com.store.popup.comment.dto.CommentSaveDto;
import com.store.popup.comment.dto.CommentUpdateReqDto;
import com.store.popup.comment.dto.ReplyCommentSaveDto;
import com.store.popup.comment.service.CommentService;
import com.store.popup.common.dto.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("comment")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/create")
    public ResponseEntity<CommonResDto> register(@RequestBody CommentSaveDto dto){
        Comment comment = commentService.createComment(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "Comment 등록 성공", comment.toDto());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    // 대댓글
    @PostMapping("/reply")
    public ResponseEntity<CommonResDto> reply(@RequestBody ReplyCommentSaveDto dto){
        Comment comment = commentService.createReplyComment(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "대댓글 등록 성공", comment.toDto());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    // 댓글 리스트 컨트롤러
    @GetMapping("/list/{id}")
    public ResponseEntity<CommonResDto> commentListByPost(@PathVariable Long id){
        List<CommentDetailDto> comments = commentService.getCommentByPostId(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post별 comment 목록 조회 성공", comments);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<CommonResDto> updateComment(@PathVariable Long id, @RequestBody CommentUpdateReqDto dto){
        commentService.updateComment(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "comment가 성공적으로 수정되었습니다.", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<CommonResDto> deleteComment(@PathVariable Long id){
        commentService.deleteComment(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "comment가 성공적으로 삭제되었습니다.", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}