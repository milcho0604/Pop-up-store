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
@AllArgsConstructor
@NoArgsConstructor
public class MemberListResDto {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private Address address;
    private String memberEmail;
    private String profileImgUrl;
    private Role role;
    private LocalDateTime deletedAt;
    private boolean isVerified;

    // 생성자
    public MemberListResDto(Long id, String name, String nickname, String memberEmail, String phone, Address address, boolean isVerified, LocalDateTime deletedAt, Role role) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.memberEmail = memberEmail;
        this.address = address;
        this.phone = phone;
        this.isVerified = isVerified;
        this.role = role;
        this.deletedAt = deletedAt;
    }
    // 엔티티로부터 DTO를 생성하는 메서드
    public static MemberListResDto fromEntity(Member member) {
        return new MemberListResDto(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getMemberEmail(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.isVerified(),
                member.getDeletedAt(),
                member.getRole()
        );
    }
}

