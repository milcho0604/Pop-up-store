package com.store.popup.pop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.domain.PostDetail;
import com.store.popup.pop.dto.OperatingHoursDto;
import com.store.popup.pop.dto.PostDetailResDto;
import com.store.popup.pop.dto.PostDetailSaveDto;
import com.store.popup.pop.dto.PostDetailUpdateDto;
import com.store.popup.pop.repository.PostDetailRepository;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostDetailService {

    private final PostDetailRepository postDetailRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    // PostDetail 생성
    public PostDetailResDto createPostDetail(Long postId, PostDetailSaveDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        // 본인 또는 관리자만 생성 가능
        checkPostOwnership(post);

        // 이미 PostDetail이 존재하는지 확인
        if (postDetailRepository.existsByPostId(postId)) {
            throw new IllegalArgumentException("이미 상세 정보가 존재합니다. 수정 API를 사용하세요.");
        }

        // 영업시간 Map을 JSON 문자열로 변환
        String operatingHoursJson = convertOperatingHoursToJson(dto.getOperatingHours());

        PostDetail postDetail = dto.toEntity(post, operatingHoursJson);
        PostDetail savedPostDetail = postDetailRepository.save(postDetail);

        return PostDetailResDto.fromEntity(savedPostDetail);
    }

    // PostDetail 조회
    @Transactional(readOnly = true)
    public PostDetailResDto getPostDetail(Long postId) {
        PostDetail postDetail = postDetailRepository.findByPostId(postId)
                .orElseThrow(() -> new EntityNotFoundException("상세 정보가 존재하지 않습니다."));

        return PostDetailResDto.fromEntity(postDetail);
    }

    // PostDetail 수정
    public PostDetailResDto updatePostDetail(Long postId, PostDetailUpdateDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        // 본인 또는 관리자만 수정 가능
        checkPostOwnership(post);

        PostDetail postDetail = postDetailRepository.findByPostId(postId)
                .orElseThrow(() -> new EntityNotFoundException("상세 정보가 존재하지 않습니다."));

        // 영업시간이 변경된 경우에만 JSON 변환
        if (dto.getOperatingHours() != null) {
            String operatingHoursJson = convertOperatingHoursToJson(dto.getOperatingHours());
            postDetail.updateOperatingHours(operatingHoursJson);
        }

        // 각 필드별 업데이트 (null이 아닌 경우만)
        if (dto.getDayOff() != null) {
            postDetail.updateDayOff(dto.getDayOff());
        }
        if (dto.getEntryFee() != null) {
            postDetail.updateEntryFee(dto.getEntryFee());
        }
        if (dto.getParkingAvailable() != null || dto.getParkingFee() != null) {
            postDetail.updateParkingInfo(dto.getParkingAvailable(), dto.getParkingFee());
        }
        if (dto.getNearbySubway() != null || dto.getNearbySubwayExit() != null) {
            postDetail.updateSubwayInfo(dto.getNearbySubway(), dto.getNearbySubwayExit());
        }

        return PostDetailResDto.fromEntity(postDetail);
    }

    // PostDetail 삭제
    public void deletePostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        // 본인 또는 관리자만 삭제 가능
        checkPostOwnership(post);

        PostDetail postDetail = postDetailRepository.findByPostId(postId)
                .orElseThrow(() -> new EntityNotFoundException("상세 정보가 존재하지 않습니다."));

        postDetail.updateDeleteAt();
    }

    // 영업시간 Map을 JSON 문자열로 변환
    private String convertOperatingHoursToJson(Map<String, OperatingHoursDto> operatingHours) {
        if (operatingHours == null || operatingHours.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(operatingHours);
        } catch (JsonProcessingException e) {
            log.error("영업시간 JSON 변환 실패: {}", e.getMessage());
            throw new IllegalArgumentException("영업시간 데이터 형식이 올바르지 않습니다.");
        }
    }

    // 포스트 소유권 확인 (본인 또는 관리자)
    private void checkPostOwnership(Post post) {
        Member currentMember = getCurrentMember();

        boolean isOwner = post.getMember().getId().equals(currentMember.getId());
        boolean isAdmin = currentMember.getRole().toString().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    // 현재 로그인한 회원 조회
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
