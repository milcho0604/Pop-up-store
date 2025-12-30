package com.store.popup.qna.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.qna.domain.Answer;
import com.store.popup.qna.domain.Question;
import com.store.popup.qna.dto.*;
import com.store.popup.qna.repository.AnswerRepository;
import com.store.popup.qna.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class QnAService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // ========== 질문 관련 ==========

    /**
     * 질문 작성
     */
    public QuestionDto createQuestion(Long postId, QuestionSaveDto dto) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Question question = Question.builder()
                .post(post)
                .member(member)
                .content(dto.getContent())
                .build();

        Question savedQuestion = questionRepository.save(question);
        return QuestionDto.fromEntity(savedQuestion);
    }

    /**
     * Post의 모든 질문 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<QuestionDto> getQuestions(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Page<Question> questions = questionRepository.findByPostWithFetch(post, pageable);
        return questions.map(QuestionDto::fromEntity);
    }

    /**
     * Post의 모든 질문 조회 (리스트)
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        List<Question> questions = questionRepository.findByPostWithFetch(post);
        return questions.stream()
                .map(QuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 질문 상세 조회 (답변 포함)
     */
    @Transactional(readOnly = true)
    public QuestionDto getQuestionDetail(Long questionId) {
        Question question = questionRepository.findByIdWithAnswer(questionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

        return QuestionDto.fromEntity(question);
    }

    /**
     * 질문 삭제
     */
    public void deleteQuestion(Long questionId) {
        Member member = getCurrentMember();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

        // 작성자 또는 관리자만 삭제 가능
        if (!question.getMember().getId().equals(member.getId()) && !member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("질문 작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // soft delete
        question.updateDeleteAt();
    }

    // ========== 답변 관련 ==========

    /**
     * 답변 작성 (게시글 작성자 또는 관리자만)
     */
    public QuestionDto createAnswer(Long questionId, AnswerSaveDto dto) {
        Member member = getCurrentMember();
        Question question = questionRepository.findByIdWithAnswer(questionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

        // 게시글 작성자 또는 관리자만 답변 가능
        if (!question.getPost().getMember().getId().equals(member.getId()) && !member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("게시글 작성자 또는 관리자만 답변할 수 있습니다.");
        }

        // 이미 답변이 있는 경우
        if (question.hasAnswer()) {
            throw new IllegalArgumentException("이미 답변이 존재합니다.");
        }

        Answer answer = Answer.builder()
                .question(question)
                .member(member)
                .content(dto.getContent())
                .build();

        answerRepository.save(answer);

        // 최신 질문 정보 반환
        Question updatedQuestion = questionRepository.findByIdWithAnswer(questionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

        return QuestionDto.fromEntity(updatedQuestion);
    }

    /**
     * 답변 수정 (답변 작성자만)
     */
    public QuestionDto updateAnswer(Long answerId, AnswerSaveDto dto) {
        Member member = getCurrentMember();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));

        // 답변 작성자만 수정 가능
        if (!answer.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("답변 작성자만 수정할 수 있습니다.");
        }

        // dirty checking
        answer.updateContent(dto.getContent());

        // 최신 질문 정보 반환
        Question question = questionRepository.findByIdWithAnswer(answer.getQuestion().getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

        return QuestionDto.fromEntity(question);
    }

    /**
     * 답변 삭제 (답변 작성자만)
     */
    public void deleteAnswer(Long answerId) {
        Member member = getCurrentMember();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));

        // 답변 작성자만 삭제 가능
        if (!answer.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("답변 작성자만 삭제할 수 있습니다.");
        }

        // soft delete
        answer.updateDeleteAt();
    }

    // ========== Helper Methods ==========

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
