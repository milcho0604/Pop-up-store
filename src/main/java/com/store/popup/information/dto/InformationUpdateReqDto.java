package com.store.popup.information.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformationUpdateReqDto {
    private String title;
    private String content;
    private MultipartFile postImage;
    
    // 팝업 스토어 운영 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    private String phoneNumber;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;
    
    // 상세 주소
    private String detailAddress;
}

