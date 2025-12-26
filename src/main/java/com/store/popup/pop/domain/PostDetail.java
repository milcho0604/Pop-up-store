package com.store.popup.pop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Post의 상세 영업 정보
 * Post와 1:1 관계
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "post_detail")
public class PostDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_detail_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    @JsonIgnore  // 순환 참조 방지
    private Post post;

    /**
     * 요일별 영업시간 (JSON 형태로 저장)
     * 예: {"MONDAY":{"open":"09:00","close":"18:00"},"TUESDAY":{"open":"09:00","close":"18:00"},...}
     */
    @Column(name = "operating_hours", columnDefinition = "TEXT")
    private String operatingHoursJson;

    /**
     * 휴무일 (예: "매주 월요일", "설날, 추석", "연중무휴")
     */
    @Column(name = "day_off", length = 500)
    private String dayOff;

    /**
     * 입장료 정보 (예: "무료", "성인 5,000원, 어린이 3,000원")
     */
    @Column(name = "entry_fee", length = 500)
    private String entryFee;

    /**
     * 주차 가능 여부
     */
    @Column(name = "parking_available")
    private Boolean parkingAvailable;

    /**
     * 주차 요금 (예: "무료", "시간당 2,000원", "3시간 무료")
     */
    @Column(name = "parking_fee", length = 500)
    private String parkingFee;

    /**
     * 가까운 지하철역 (예: "강남역")
     */
    @Column(name = "nearby_subway", length = 200)
    private String nearbySubway;

    /**
     * 가까운 지하철 출구 (예: "1번 출구", "2번 출구에서 도보 5분")
     */
    @Column(name = "nearby_subway_exit", length = 200)
    private String nearbySubwayExit;

    // 업데이트 메서드
    public void updateOperatingHours(String operatingHoursJson) {
        if (operatingHoursJson != null) {
            this.operatingHoursJson = operatingHoursJson;
        }
    }

    public void updateDayOff(String dayOff) {
        this.dayOff = dayOff;
    }

    public void updateEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public void updateParkingInfo(Boolean parkingAvailable, String parkingFee) {
        if (parkingAvailable != null) {
            this.parkingAvailable = parkingAvailable;
        }
        this.parkingFee = parkingFee;
    }

    public void updateSubwayInfo(String nearbySubway, String nearbySubwayExit) {
        this.nearbySubway = nearbySubway;
        this.nearbySubwayExit = nearbySubwayExit;
    }
}
