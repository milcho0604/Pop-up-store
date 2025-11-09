package com.store.popup.information.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.information.dto.InformationListDto;
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
                .build();
    }

    // 상태 변경 메서드
    public void approve() {
        this.status = InformationStatus.APPROVED;
    }

    public void reject() {
        this.status = InformationStatus.REJECTED;
    }
}

