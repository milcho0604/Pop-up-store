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
        Member member = getCurrentMember();

        if (StringUtils.hasText(dto.getName())) {
            member.changeName(dto.getName());
        }
        if (StringUtils.hasText(dto.getNickname())) {
            member.changeNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getPhoneNumber())) {
            member.changePhoneNumber(dto.getPhoneNumber());
        }

        boolean hasCity   = StringUtils.hasText(dto.getCity());
        boolean hasStreet = StringUtils.hasText(dto.getStreet());
        boolean hasZip    = StringUtils.hasText(dto.getZipcode());
        if (hasCity || hasStreet || hasZip) {
            String currentCity    = member.getAddress() != null ? member.getAddress().getCity() : null;
            String currentStreet  = member.getAddress() != null ? member.getAddress().getStreet() : null;
            String currentZipcode = member.getAddress() != null ? member.getAddress().getZipcode() : null;

            Address newAddress = Address.builder()
                    .city(hasCity ? dto.getCity() : currentCity)
                    .street(hasStreet ? dto.getStreet() : currentStreet)
                    .zipcode(hasZip ? dto.getZipcode() : currentZipcode)
                    .build();
            member.changeAddress(newAddress);
        }

        if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            String imageUrl = s3ClientFileUpload.upload(dto.getProfileImage());
            member.changeProfileImgUrl(imageUrl);
        }

        memberRepository.save(member);
        return MemberProfileResDto.fromEntity(member);
    }
}
