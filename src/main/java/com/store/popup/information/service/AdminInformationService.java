package com.store.popup.information.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.repository.InformationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AdminInformationService {

    private final InformationRepository informationRepository;
    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;

    // 관리자가 제보 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<InformationListDto> getInformationList(Pageable pageable, InformationStatus status) {
        checkAdminRole();

        Page<Information> informations;
        if (status != null) {
            informations = informationRepository.findByStatusAndDeletedAtIsNull(status, pageable);
        } else {
            informations = informationRepository.findByDeletedAtIsNull(pageable);
        }

        return informations.map(Information::listFromEntity);
    }

    // 관리자가 제보 상세 조회
    @Transactional(readOnly = true)
    public InformationDetailDto getInformationDetail(Long id) {
        checkAdminRole();

        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 제보입니다."));

        return InformationDetailDto.fromEntity(information);
    }

    // 관리자 권한 체크
    private void checkAdminRole() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
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

