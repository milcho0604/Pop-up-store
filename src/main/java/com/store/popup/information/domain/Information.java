package com.store.popup.information.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.common.enumdir.Category;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.dto.InformationUpdateReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Information extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "information_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String content;

    @Column
    private String postImgUrl;

    @Column
    private String phoneNumber;

    // 제보자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Enumerated(EnumType.STRING)
    @Column
    private Category category;

    // 팝업 스토어 운영 기간
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    // 팝업 스토어 주소
    @Embedded
    private Address address;

    // 제보 상태 (PENDING: 대기중, APPROVED: 승인됨, REJECTED: 거부됨)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InformationStatus status = InformationStatus.PENDING;

    public InformationListDto listFromEntity() {
        return InformationListDto.builder()
                .id(this.id)
                .title(this.title)
                .reporterEmail(this.reporter.getMemberEmail())
                .reporterNickname(this.reporter.getNickname())
                .content(this.content)
                .postImgUrl(this.postImgUrl)
                .createdTimeAt(this.getCreatedAt())
                .startDate(this.startDate)
                .endDate(this.endDate)
                .city(this.address != null ? this.address.getCity() : null)
                .street(this.address != null ? this.address.getStreet() : null)
                .zipcode(this.address != null ? this.address.getZipcode() : null)
                .detailAddress(this.address != null ? this.address.getDetailAddress() : null)
                .status(this.status)
                .category(this.category)
                .build();
    }

    // 상태 변경 메서드 : 승인
    public void approve() {
        this.status = InformationStatus.APPROVED;
    }

    // 상태 변경 메서드 : 거절
    public void reject() {
        this.status = InformationStatus.REJECTED;
    }

    // 이미지 업데이트 메서드
    public void updateImage(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    // 정보 업데이트 메서드
    public void update(InformationUpdateReqDto dto) {
        // 제목
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        // 내용
        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }
        // 카테고리
        if (dto.getCategory() != null) {
            this.category = dto.getCategory();
        }
        // 전화번호
        if (dto.getPhoneNumber() != null) {
            this.phoneNumber = dto.getPhoneNumber();
        }
        // 시작일
        if (dto.getStartDate() != null) {
            this.startDate = dto.getStartDate();
        }
        // 종료일
        if (dto.getEndDate() != null) {
            this.endDate = dto.getEndDate();
        }
        // 주소 병합 (기존 address 유지 + 들어온 값만 반영)
        if (dto.getCity() != null || dto.getStreet() != null || dto.getZipcode() != null || dto.getDetailAddress() != null) {
            String currentCity = this.address != null ? this.address.getCity() : null;
            String currentStreet = this.address != null ? this.address.getStreet() : null;
            String currentZipcode = this.address != null ? this.address.getZipcode() : null;
            String currentDetailAddress = this.address != null ? this.address.getDetailAddress() : null;

            this.address = Address.builder()
                    .city(dto.getCity() != null ? dto.getCity() : currentCity)
                    .street(dto.getStreet() != null ? dto.getStreet() : currentStreet)
                    .zipcode(dto.getZipcode() != null ? dto.getZipcode() : currentZipcode)
                    .detailAddress(dto.getDetailAddress() != null ? dto.getDetailAddress() : currentDetailAddress)
                    .build();
        }
    }
}

