package com.store.popup.information.service;

import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.dto.InformationSaveDto;
import com.store.popup.information.dto.InformationUpdateReqDto;
import com.store.popup.information.repository.InformationRepository;
import com.store.popup.tag.domain.Tag;
import com.store.popup.tag.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InformationService {

    private final InformationRepository informationRepository;
    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;
    private final TagService tagService;

    // 고객이 팝업 스토어 제보
    public Information create(InformationSaveDto dto) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member reporter = findMemberByEmail(memberEmail);

        int reportCount = reporter.getReportCount();

        // 신고 횟수가 5 이상일 경우 예외 처리
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 포스트를 작성할 수 없습니다.");
        }

        String postImgUrl = null;
        if (dto.getPostImage() != null && !dto.getPostImage().isEmpty()) {
            postImgUrl = s3ClientFileUpload.upload(dto.getPostImage());
        }

        // 태그 처리
        List<Tag> tags = tagService.findOrCreateTags(dto.getTagNames());

        Information information = dto.toEntity(postImgUrl, reporter, tags);
        return informationRepository.save(information);
    }

    // 고객이 자신이 제보한 팝업 스토어 정보 수정
    public InformationDetailDto update(Long id, InformationUpdateReqDto dto) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member reporter = findMemberByEmail(memberEmail);

        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 제보입니다."));

        // 본인의 제보인지 확인
        if (!information.getReporter().getId().equals(reporter.getId())) {
            throw new IllegalArgumentException("본인의 제보만 수정할 수 있습니다.");
        }

        // 이미 승인되거나 거부된 제보는 수정 불가
        if (information.getStatus() != InformationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 제보만 수정할 수 있습니다.");
        }

        // 이미지 업로드 처리
        if (dto.getPostImage() != null && !dto.getPostImage().isEmpty()) {
            String postImgUrl = s3ClientFileUpload.upload(dto.getPostImage());
            information.updateImage(postImgUrl);
        }

        // 태그 업데이트 처리
        if (dto.getTagNames() != null) {
            List<Tag> tags = tagService.findOrCreateTags(dto.getTagNames());
            information.updateTags(tags);
        }

        // 정보 업데이트
        information.update(dto);

        Information savedInformation = informationRepository.save(information);
        return InformationDetailDto.fromEntity(savedInformation);
    }

    // 제보자 본인이 자신의 제보 목록 조회
    @Transactional(readOnly = true)
    public List<InformationListDto> getMyInformationList() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member reporter = findMemberByEmail(memberEmail);

        List<Information> informations = informationRepository.findAll().stream()
                .filter(info -> info.getReporter().getId().equals(reporter.getId()))
                .filter(info -> info.getDeletedAt() == null)
                .collect(Collectors.toList());

        return informations.stream()
                .map(Information::listFromEntity)
                .collect(Collectors.toList());
    }

    // 제보자 본인이 자신의 제보 상세 조회
    @Transactional(readOnly = true)
    public InformationDetailDto getMyInformationDetail(Long id) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member reporter = findMemberByEmail(memberEmail);

        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 제보입니다."));

        if (!information.getReporter().getId().equals(reporter.getId())) {
            throw new IllegalArgumentException("본인의 제보만 조회할 수 있습니다.");
        }

        return InformationDetailDto.fromEntity(information);
    }

    // 멤버 객체 반환
    private Member findMemberByEmail(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }

    // ID 리스트로 Information 조회 (Post 변환용)
    public List<Information> findByIds(List<Long> ids) {
        return informationRepository.findByIdIn(ids);
    }

    // Information 저장 (Post 변환 시 상태 업데이트용)
    public Information save(Information information) {
        return informationRepository.save(information);
    }
}

