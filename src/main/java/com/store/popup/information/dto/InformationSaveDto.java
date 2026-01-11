package com.store.popup.information.dto;

import com.store.popup.tag.domain.Tag;
import com.store.popup.common.enumdir.Category;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.information.domain.Information;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformationSaveDto {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
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
    private String dong;
    private String street;
    private String zipcode;

    // 상세 주소
    private String detailAddress;

    // 카테고리
    private Category category;

    // 태그
    private List<String> tagNames;

    public Information toEntity(String postImgUrl, Member reporter, List<Tag> tags) {
        Address address = null;
        if (city != null || dong != null || street != null || zipcode != null || detailAddress != null) {
            address = Address.builder()
                    .city(city)
                    .dong(dong)
                    .street(street)
                    .zipcode(zipcode)
                    .detailAddress(detailAddress)
                    .build();
        }

        return Information.builder()
                .reporter(reporter)
                .title(this.title)
                .content(this.content)
                .postImgUrl(postImgUrl)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .address(address)
                .phoneNumber(this.phoneNumber)
                .category(this.category)
                .tags(tags != null ? tags : new ArrayList<>())
                .build();
    }
}

