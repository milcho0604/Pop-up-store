package com.store.popup.comment.controller;

import com.store.popup.comment.domain.Comment;
import com.store.popup.comment.dto.CommentSaveDto;
import com.store.popup.comment.dto.ReplyCommentSaveDto;
import com.store.popup.comment.service.CommentService;
import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("comment")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> register(@RequestBody CommentSaveDto dto){
        try {
            Comment comment = commentService.createComment(dto);
            if (dto.getPostId() != null){
                CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED,"Comment 등록 성공", comment);
                return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
            }else {
                throw new IllegalArgumentException("postId must be provided.");
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody ReplyCommentSaveDto dto){
        try {
            Comment comment = commentService.createReplyComment(dto);
            if (dto.getPostId() != null){
                CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "대댓글 등록 성공", comment);
                return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
            } else {
                throw new IllegalArgumentException("postId must be provided.");
            }
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage()) ;
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

}
