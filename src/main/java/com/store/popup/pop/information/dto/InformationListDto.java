package com.store.popup.pop.information.dto;

import com.store.popup.pop.information.domain.InformationStatus;
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
    private InformationStatus status;
}

