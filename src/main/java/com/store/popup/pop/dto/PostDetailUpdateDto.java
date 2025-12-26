package com.store.popup.pop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * PostDetail 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailUpdateDto {

    private Map<String, OperatingHoursDto> operatingHours; // 요일별 영업시간 (MONDAY, TUESDAY, ...)
    private String dayOff;                                  // 휴무일 정보
    private String entryFee;                                // 입장료 정보
    private Boolean parkingAvailable;                       // 주차 가능 여부
    private String parkingFee;                              // 주차 요금
    private String nearbySubway;                            // 가까운 지하철역
    private String nearbySubwayExit;                        // 가까운 지하철 출구
}
