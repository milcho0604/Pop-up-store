package com.store.popup.member.service;

import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.member.dto.MemberProfileResDto;
import com.store.popup.member.dto.MemberProfileUpdateReqDto;
import com.store.popup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        if (!hasCity && !hasStreet && !hasZip) return;

        String currentCity = member.getAddress() != null ? member.getAddress().getCity() : null;
        String currentStreet = member.getAddress() != null ? member.getAddress().getStreet() : null;
        String currentZipcode = member.getAddress() != null ? member.getAddress().getZipcode() : null;

        Address newAddress = Address.builder()
                .city(hasCity ? dto.getCity() : currentCity)
                .street(hasStreet ? dto.getStreet() : currentStreet)
                .zipcode(hasZip ? dto.getZipcode() : currentZipcode)
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

/* 전화번호 유효성 검사
private void validatePhone(String phone) {
    if (!phone.matches("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$")) {
        throw new IllegalArgumentException("전화번호 형식이 유효하지 않습니다.");
    }
}
*/

}
