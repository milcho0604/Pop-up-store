package com.store.popup.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateReqDto {
    private String name;
    private String nickname;
    private String phoneNumber;
    private String city;
    private String street;
    private String zipcode;
    private MultipartFile profileImage;
}
