package com.store.popup.pop.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateReqDto {
    private String memberEmail;
    private String title;
    private String content;
    private MultipartFile postImg;
    private LocalDateTime updateTime;
    
    // 내부적으로 사용할 LocalDateTime 필드들
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;
    
    // 문자열을 LocalDateTime으로 변환하는 메서드

}
