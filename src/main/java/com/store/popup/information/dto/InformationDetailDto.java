package com.store.popup.information.dto;

import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationDetailDto {
    private Long id;
    private String reporterEmail;
    private String reporterNickname;
    private String title;
    private String content;
    private String postImgUrl;
    private LocalDateTime createdTimeAt;
    private LocalDateTime updatedTimeAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String city;
    private String street;
    private String zipcode;
    private InformationStatus status;
    private String phoneNumber;

    public static InformationDetailDto fromEntity(Information information) {
        return InformationDetailDto.builder()
                .id(information.getId())
                .reporterEmail(information.getReporter().getMemberEmail())
                .reporterNickname(information.getReporter().getNickname())
                .title(information.getTitle())
                .content(information.getContent())
                .postImgUrl(information.getPostImgUrl())
                .createdTimeAt(information.getCreatedAt())
                .updatedTimeAt(information.getUpdatedAt())
                .startDate(information.getStartDate())
                .endDate(information.getEndDate())
                .city(information.getAddress() != null ? information.getAddress().getCity() : null)
                .street(information.getAddress() != null ? information.getAddress().getStreet() : null)
                .zipcode(information.getAddress() != null ? information.getAddress().getZipcode() : null)
                .status(information.getStatus())
                .phoneNumber(information.getPhoneNumber())
                .build();
    }
}

