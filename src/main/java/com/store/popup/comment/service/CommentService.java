package com.store.popup.comment.service;

import com.store.popup.comment.domain.Comment;
import com.store.popup.comment.dto.CommentDetailDto;
import com.store.popup.comment.dto.CommentSaveDto;
import com.store.popup.comment.dto.CommentUpdateReqDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            savedComment = dto.toEntity(post, null, member.getMemberEmail(), member.getNickname(), member.getProfileImgUrl());
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
            savedComment = dto.toEntity(post, parentComment, member.getMemberEmail(), member.getNickname(), member.getProfileImgUrl());
            commentRepository.save(savedComment);
        }else {
            throw new IllegalArgumentException("답변을 위한 POST ID가 필요합니다.");
        }
        return savedComment;
    }

    // 댓글 업데이트
    public void updateComment(Long id, CommentUpdateReqDto dto){
        Member member  = memberAuthService.getCurrentMember();
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 comment입니다."));
        int reportCount = member.getReportCount();
        // 신고 횟수가 5 이상일 경우 예외 처리
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 댓글을 수정할 수 없습니다.");
        }

        //현재 로그인 되어있는 사용자가 comment의 작성자인 경우
        if (!Objects.equals(member.getMemberEmail(), comment.getMemberEmail())){
            throw new IllegalArgumentException("작성자 이외에는 수정할 수 없습니다.");
        }
        comment.update(dto);
    }

    // 댓글 리스트
    public List<CommentDetailDto> getCommentByPostId(Long postId){
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .map(comment -> CommentDetailDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .doctorEmail(comment.getMemberEmail())
                        .nickName(comment.getNickName())
                        .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                        .profileImg(comment.getProfileImg())
                        .createdTimeAt(comment.getCreatedAt())
                        .updatedTimeAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    // 댓글 삭제
    public void deleteComment(Long id){
        Member member  = memberAuthService.getCurrentMember();
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 comment입니다."));
        int reportCount = member.getReportCount();
        // 신고 횟수가 5 이상일 경우 예외 처리
        if (!member.getMemberEmail().equals(comment.getMemberEmail())){
            throw new IllegalArgumentException("작성자 이외에는 삭제할 수 없습니다.");
        }
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 댓글을 삭제할 수 없습니다.");
        }
        comment.updateDeleteAt();
    }

}

