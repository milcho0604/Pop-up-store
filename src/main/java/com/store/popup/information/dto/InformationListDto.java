package com.store.popup.information.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.tag.dto.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private String dong;
    private String street;
    private String zipcode;

    // 상세 주소
    private String detailAddress;

    private InformationStatus status;

    // 카테고리
    private Category category;

    // 태그 목록
    private List<TagDto> tags;
}

