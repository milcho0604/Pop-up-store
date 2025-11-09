package com.store.popup.member.service;

import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.member.dto.MemberProfileResDto;
import com.store.popup.member.dto.MemberProfileUpdateReqDto;
import com.store.popup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;
    private final PasswordEncoder passwordEncoder;

    // 현재 유저 정보 반환하는 dto
    private Member getCurrentMember() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmailOrThrow(email);
    }

    // 현재 멤버의 기본 정보를 반환
    @Transactional(readOnly = true)
    public MemberProfileResDto getMyProfile() {
        Member member = getCurrentMember();
        return MemberProfileResDto.fromEntity(member);
    }

    // 회원 정보 수정
    @Transactional
    public MemberProfileResDto updateMyProfile(MemberProfileUpdateReqDto dto) {
        Member member = getCurrentMember(); // 기존 그대로 사용 가정

        applyBasicInfoPatch(member, dto);
        applyAddressPatch(member, dto);
        applyProfileImagePatch(member, dto);

        memberRepository.save(member);
        return MemberProfileResDto.fromEntity(member);
    }

    /* ================== Private 역할 메서드들 ================== */

    // 1) 기본 정보: 이름/닉네임/전화
    private void applyBasicInfoPatch(Member member, MemberProfileUpdateReqDto dto) {
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            validatePassword(dto.getPassword(), dto.getConfirmPassword(), member.getPassword());
            member.changePassword(passwordEncoder.encode(dto.getPassword()));
            System.out.println("비밀번호가 변경되었습니다.");
        }
        if (StringUtils.hasText(dto.getName())) {
            member.changeName(dto.getName());
        }

        if (StringUtils.hasText(dto.getNickname())) {
            if (memberRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            member.changeNickname(dto.getNickname());
        }

        if (StringUtils.hasText(dto.getPhoneNumber())) {
            // 필요시 형식 검증: validatePhone(dto.getPhoneNumber());
            member.changePhoneNumber(dto.getPhoneNumber());
        }
    }

    // 2) 주소 Patch: 일부 필드만 들어와도 기존 값 보존
    private void applyAddressPatch(Member member, MemberProfileUpdateReqDto dto) {
        boolean hasCity = StringUtils.hasText(dto.getCity());
        boolean hasStreet = StringUtils.hasText(dto.getStreet());
        boolean hasZip = StringUtils.hasText(dto.getZipcode());
        boolean hasDetailAddress = StringUtils.hasText(dto.getDetailAddress());

        if (!hasCity && !hasStreet && !hasZip && !hasDetailAddress) return;

        String currentCity = member.getAddress() != null ? member.getAddress().getCity() : null;
        String currentStreet = member.getAddress() != null ? member.getAddress().getStreet() : null;
        String currentZipcode = member.getAddress() != null ? member.getAddress().getZipcode() : null;
        String currentDetailAddress = member.getAddress() != null ? member.getAddress().getDetailAddress() : null;

        Address newAddress = Address.builder()
                .city(hasCity ? dto.getCity() : currentCity)
                .street(hasStreet ? dto.getStreet() : currentStreet)
                .zipcode(hasZip ? dto.getZipcode() : currentZipcode)
                .detailAddress(hasDetailAddress ? dto.getDetailAddress() : currentDetailAddress)
                .build();

        member.changeAddress(newAddress);
    }

    // 3) 프로필 이미지 업로드/적용
    private void applyProfileImagePatch(Member member, MemberProfileUpdateReqDto dto) {
        if (dto.getProfileImage() == null || dto.getProfileImage().isEmpty()) return;

        // I/O 경계 분리: 실패 시 로깅/예외 처리 지점
        String imageUrl = s3ClientFileUpload.upload(dto.getProfileImage());
        member.changeProfileImgUrl(imageUrl);
    }

    // 비밀번호 확인 및 검증 로직
    private void validatePassword(String newPassword, String confirmPassword, String currentPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("동일하지 않은 비밀번호 입니다.");
        }

        if (newPassword.length() <= 7) {
            throw new RuntimeException("비밀번호는 8자 이상이어야 합니다.");
        }

        if (passwordEncoder.matches(newPassword, currentPassword)) {
            throw new RuntimeException("이전과 동일한 비밀번호로 설정할 수 없습니다.");
        }
    }

}
