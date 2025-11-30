package com.store.popup.comment.service;

import com.store.popup.comment.domain.Comment;
import com.store.popup.comment.dto.CommentSaveDto;
import com.store.popup.comment.dto.ReplyCommentSaveDto;
import com.store.popup.comment.repository.CommentRepository;
import com.store.popup.member.domain.Member;
import com.store.popup.member.service.MemberAuthService;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberAuthService memberAuthService;
    private final PostRepository postRepository;

    //    댓글 작성 메소드
    public Comment createComment(CommentSaveDto dto){
        Comment savedComment;
        Member member  = memberAuthService.getCurrentMember();

        int reportCount = member.getReportCount();
        // 신고 횟수가 5 이상일 경우 예외 처리
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 댓글을 작성할 수 없습니다.");
        }

        if (dto.getPostId() != null){
            Post post = postRepository.findById(dto.getPostId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 post입니다."));
            savedComment = dto.toEntity(post, null, member.getMemberEmail(), member.getName(), member.getProfileImgUrl());
            commentRepository.save(savedComment);
        }else {
            throw new IllegalArgumentException("답변을 위한 POST ID가 필요합니다.");
        }
        return savedComment;
    }

    // 대댓글 API
    public Comment createReplyComment(ReplyCommentSaveDto dto){
        Member member  = memberAuthService.getCurrentMember();
        Comment savedComment;
        int reportCount = member.getReportCount();
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 대댓글을 작성할 수 없습니다.");
        }
        if (dto.getPostId() != null){
            Post post = postRepository.findById(dto.getPostId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 post입니다."));
            Comment parentComment = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));
            if(parentComment.getParent() != null){
                throw new IllegalArgumentException("대댓글에는 댓글이 허용되지 않습니다.");
            }
            savedComment = dto.toEntity(post, parentComment, member.getMemberEmail(), member.getName(), member.getProfileImgUrl());
            commentRepository.save(savedComment);
        }else {
            throw new IllegalArgumentException("답변을 위한 POST ID가 필요합니다.");
        }
        return savedComment;
    }
}

