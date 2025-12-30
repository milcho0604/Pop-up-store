package com.store.popup.poll.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.poll.domain.Poll;
import com.store.popup.poll.domain.PollOption;
import com.store.popup.poll.domain.PollVote;
import com.store.popup.poll.dto.*;
import com.store.popup.poll.repository.PollOptionRepository;
import com.store.popup.poll.repository.PollRepository;
import com.store.popup.poll.repository.PollVoteRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // ========== 관리자 전용 기능 ==========

    // 투표 생성 (관리자만)
    public PollResDto createPoll(PollSaveDto dto) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        // 투표 기간 유효성 검증
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("투표 종료일은 시작일보다 늦어야 합니다.");
        }

        Poll poll = dto.toEntity(admin);
        Poll savedPoll = pollRepository.save(poll);

        return PollResDto.fromEntity(savedPoll);
    }

    // 투표에 선택지 추가 (관리자만)
    public PollResDto addPollOption(Long pollId, PollOptionSaveDto dto) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Poll poll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        PollOption pollOption = PollOption.builder()
                .poll(poll)
                .post(post)
                .description(dto.getDescription())
                .build();

        pollOptionRepository.save(pollOption);

        // Poll을 다시 조회하여 최신 options 포함
        Poll updatedPoll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        return PollResDto.fromEntity(updatedPoll);
    }

    // 투표 수정 (관리자만)
    public PollResDto updatePoll(Long pollId, PollUpdateDto dto) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        // 투표 기간 유효성 검증
        LocalDateTime newStartDate = dto.getStartDate() != null ? dto.getStartDate() : poll.getStartDate();
        LocalDateTime newEndDate = dto.getEndDate() != null ? dto.getEndDate() : poll.getEndDate();
        if (newEndDate.isBefore(newStartDate)) {
            throw new IllegalArgumentException("투표 종료일은 시작일보다 늦어야 합니다.");
        }

        // dirty checking
        poll.update(dto.getTitle(), dto.getDescription(), dto.getStartDate(),
                dto.getEndDate(), dto.getMultipleChoice());

        Poll updatedPoll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        return PollResDto.fromEntity(updatedPoll);
    }

    // 투표 삭제 (관리자만)
    public void deletePoll(Long pollId) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        // soft delete
        poll.updateDeleteAt();
    }

    // 선택지 삭제 (관리자만)
    public void deletePollOption(Long optionId) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        PollOption pollOption = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 선택지입니다."));

        // soft delete
        pollOption.updateDeleteAt();
    }

    // 모든 투표 조회 (관리자용)
    @Transactional(readOnly = true)
    public Page<PollResDto> getAllPolls(Pageable pageable) {
        Member admin = getCurrentMember();
        checkAdminRole(admin);

        Page<Poll> polls = pollRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
        return polls.map(PollResDto::fromEntity);
    }

    // ========== 공개 API ==========

    // 현재 진행중인 투표 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<PollResDto> getActivePolls(Pageable pageable) {
        Page<Poll> polls = pollRepository.findActivePolls(LocalDateTime.now(), pageable);
        return polls.map(PollResDto::fromEntity);
    }

    // 현재 진행중인 투표 조회 (리스트)
    @Transactional(readOnly = true)
    public List<PollResDto> getActivePollsList() {
        List<Poll> polls = pollRepository.findActivePollsList(LocalDateTime.now());
        return polls.stream()
                .map(PollResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 투표 상세 조회 (결과 포함)
    @Transactional(readOnly = true)
    public PollResDto getPollDetail(Long pollId) {
        Poll poll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        if (poll.getDeletedAt() != null) {
            throw new EntityNotFoundException("삭제된 투표입니다.");
        }

        return PollResDto.fromEntity(poll);
    }

    // 투표하기
    public PollResDto vote(Long pollId, VoteReqDto dto) {
        Member member = getCurrentMember();

        Poll poll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        // 투표 진행중인지 확인
        if (!poll.isActive()) {
            throw new IllegalArgumentException("투표 기간이 아닙니다.");
        }

        // 이미 투표했는지 확인
        if (pollVoteRepository.existsByPollAndMemberAndDeletedAtIsNull(poll, member)) {
            throw new IllegalArgumentException("이미 투표하셨습니다.");
        }

        // 복수 선택 체크
        if (!poll.getMultipleChoice() && dto.getOptionIds().size() > 1) {
            throw new IllegalArgumentException("단일 선택 투표입니다.");
        }

        // 투표 처리
        List<PollVote> votes = new ArrayList<>();
        for (Long optionId : dto.getOptionIds()) {
            PollOption pollOption = pollOptionRepository.findById(optionId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 선택지입니다."));

            // 해당 Poll의 선택지인지 확인
            if (!pollOption.getPoll().getId().equals(pollId)) {
                throw new IllegalArgumentException("올바르지 않은 선택지입니다.");
            }

            // 투표 기록 생성
            PollVote vote = PollVote.builder()
                    .poll(poll)
                    .pollOption(pollOption)
                    .member(member)
                    .build();
            votes.add(vote);

            // 선택지 득표수 증가 (dirty checking)
            pollOption.incrementVoteCount();
        }

        pollVoteRepository.saveAll(votes);

        // Poll 총 투표수 증가 (dirty checking)
        poll.incrementTotalVotes();

        // 최신 결과 반환
        Poll updatedPoll = pollRepository.findByIdWithOptions(pollId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 투표입니다."));

        return PollResDto.fromEntity(updatedPoll);
    }

    // 내가 참여한 투표 목록
    @Transactional(readOnly = true)
    public List<PollResDto> getMyVotes() {
        Member member = getCurrentMember();

        List<PollVote> votes = pollVoteRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);

        // Poll 중복 제거 후 변환
        List<Poll> polls = votes.stream()
                .map(PollVote::getPoll)
                .distinct()
                .collect(Collectors.toList());

        return polls.stream()
                .map(poll -> {
                    // options 포함하여 다시 조회
                    Poll pollWithOptions = pollRepository.findByIdWithOptions(poll.getId())
                            .orElse(poll);
                    return PollResDto.fromEntity(pollWithOptions);
                })
                .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    // 관리자 권한 확인
    private void checkAdminRole(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }

    // 현재 로그인한 회원 조회
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
