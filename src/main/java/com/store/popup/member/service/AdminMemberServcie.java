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
    @Transactional(readOnly = true)
    public Page<MemberListResDto> memberList(String email, String roleString, Pageable pageable) {
        Role role = null;
        Member member = memberRepository.findByMemberEmailOrThrow(email);
        Boolean isDeleted = member.getDeletedAt() != null;
        Boolean isVerified = member.isVerified();

        if (roleString != null && !roleString.isEmpty()) {
            role = Role.valueOf(roleString); // 문자열을 Role enum으로 변환
        }
        System.out.println(role);

        if (isDeleted) {
            // 탈퇴 회원 조회
            if (isVerified) {
                // 인증된 탈퇴 회원 조회 및 Role 필터링
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNullAndRole(true, role, pageable).map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNull(true, pageable).map(MemberListResDto::fromEntity);
                }
            } else {
                // 미인증 탈퇴 회원 조회 및 Role 필터링
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNullAndRole(false, role, pageable).map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNotNull(false, pageable).map(MemberListResDto::fromEntity);
                }
            }
        } else {
            // 정상 회원 조회
            if (isVerified) {
                // 인증된 정상 회원 조회 및 Role 필터링
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNullAndRole(true, role, pageable).map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNull(true, pageable).map(MemberListResDto::fromEntity);
                }
            } else {
                // 미인증 정상 회원 조회 및 Role 필터링
                if (role != null) {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNullAndRole(false, role, pageable).map(MemberListResDto::fromEntity);
                } else {
                    return memberRepository.findByIsVerifiedAndDeletedAtIsNull(false, pageable).map(MemberListResDto::fromEntity);
                }
            }
        }
    }
}
