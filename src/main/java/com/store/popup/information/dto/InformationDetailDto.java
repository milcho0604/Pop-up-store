package com.store.popup.information.dto;

import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.tag.dto.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String dong;
    private String street;
    private String zipcode;

    // 상세 주소
    private String detailAddress;
    
    private InformationStatus status;
    private String phoneNumber;

    // 태그 목록
    private List<TagDto> tags;

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
                .dong(information.getAddress() != null ? information.getAddress().getDong() : null)
                .street(information.getAddress() != null ? information.getAddress().getStreet() : null)
                .zipcode(information.getAddress() != null ? information.getAddress().getZipcode() : null)
                .detailAddress(information.getAddress() != null ? information.getAddress().getDetailAddress() : null)
                .status(information.getStatus())
                .phoneNumber(information.getPhoneNumber())
                .tags(information.getTags() != null ?
                    information.getTags().stream()
                        .map(TagDto::fromEntity)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}

