package com.store.popup.member.service;

import com.store.popup.common.config.JwtTokenProvider;
import com.store.popup.common.enumdir.Role;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.dto.MemberListResDto;
import com.store.popup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberServcie {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // 멤버 전체 목록 조회 -> 멤버 필터
    /**
     * 관리자용 회원 조회 (조건부 검색)
     *
     * @param isVerified 인증 여부 (null 허용 → 조건 없음)
     * @param isDeleted  탈퇴 여부 (null 허용 → 조건 없음)
     * @param roleString Role 필터 (null 또는 "" → 조건 없음)
     * @param pageable   페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<MemberListResDto> memberList(Boolean isVerified, Boolean isDeleted, String roleString, Pageable pageable) {
        Role role = (roleString != null && !roleString.isEmpty()) ? Role.valueOf(roleString) : null;

        if (Boolean.TRUE.equals(isDeleted)) {
            // 탈퇴 회원
            if (isVerified != null) {
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNullAndRole(isVerified, role, pageable)
                            .map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNull(isVerified, pageable)
                            .map(MemberListResDto::fromEntity);
                }
            }
        } else {
            // 정상 회원
            if (isVerified != null) {
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNullAndRole(isVerified, role, pageable)
                            .map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNull(isVerified, pageable)
                            .map(MemberListResDto::fromEntity);
                }
            } else if (role != null) {
                return memberRepository.findByRoleAndDeletedAtIsNull(role, pageable)
                        .map(MemberListResDto::fromEntity);
            } else {
                return memberRepository.findAll(pageable).map(MemberListResDto::fromEntity);
            }
        }

        // 기본값 → 전체 조회
        return memberRepository.findAll(pageable).map(MemberListResDto::fromEntity);
    }
}
