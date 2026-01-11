package com.store.popup.member.dto;

import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileResDto {
    private Long id;
    private String memberEmail;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String profileImgUrl;
    private String city;
    private String dong;
    private String street;
    private String zipcode;
    private String detailAddress;
    private Role role;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberProfileResDto fromEntity(Member member) {
        Address addr = member.getAddress();
        return MemberProfileResDto.builder()
                .id(member.getId())
                .memberEmail(member.getMemberEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .profileImgUrl(member.getProfileImgUrl())
                .city(addr != null ? addr.getCity() : null)
                .dong(addr != null ? addr.getDong() : null)
                .street(addr != null ? addr.getStreet() : null)
                .zipcode(addr != null ? addr.getZipcode() : null)
                .detailAddress(addr != null ? addr.getDetailAddress() : null)
                .role(member.getRole())
                .verified(member.isVerified())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
