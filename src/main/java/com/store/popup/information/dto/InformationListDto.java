package com.store.popup.information.dto;

import com.store.popup.information.domain.InformationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationListDto {
    private Long id;
    private String reporterEmail;
    private String reporterNickname;
    private String title;
    private String content;
    private String postImgUrl;
    private LocalDateTime createdTimeAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String city;
    private String street;
    private String zipcode;
    
    // 상세 주소
    private String detailAddress;
    
    private InformationStatus status;
}

