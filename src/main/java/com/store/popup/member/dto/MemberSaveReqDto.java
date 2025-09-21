package com.store.popup.member.dto;

import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveReqDto {
    private String name;
    private String memberEmail;
    private String password;
    private String profileImgUrl;
    private String phoneNumber;
    private String ssn;
    private Address address;
    private MultipartFile profileImage;

    @Builder.Default
    private boolean verified = true;

    @Builder.Default
    private Role role = Role.MEMBER;

    public Member toEntity(String password) {
        return Member.builder()
                .password(password)
                .name(this.name)
                .memberEmail(this.memberEmail)
                .profileImgUrl(this.profileImgUrl)
                .phoneNumber(this.phoneNumber)
                .isVerified(this.verified)
                .ssn(this.ssn)
                .address(this.address)
                .role(this.role)
                .build();
    }
}

