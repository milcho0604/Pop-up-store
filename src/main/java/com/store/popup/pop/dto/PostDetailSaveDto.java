package com.store.popup.pop.dto;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.domain.PostDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * PostDetail 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailSaveDto {

    private Map<String, OperatingHoursDto> operatingHours; // 요일별 영업시간 (MONDAY, TUESDAY, ...)
    private String dayOff;                                  // 휴무일 정보
    private String entryFee;                                // 입장료 정보
    private Boolean parkingAvailable;                       // 주차 가능 여부
    private String parkingFee;                              // 주차 요금
    private String nearbySubway;                            // 가까운 지하철역
    private String nearbySubwayExit;                        // 가까운 지하철 출구

    public PostDetail toEntity(Post post, String operatingHoursJson) {
        return PostDetail.builder()
                .post(post)
                .operatingHoursJson(operatingHoursJson)
                .dayOff(this.dayOff)
                .entryFee(this.entryFee)
                .parkingAvailable(this.parkingAvailable)
                .parkingFee(this.parkingFee)
                .nearbySubway(this.nearbySubway)
                .nearbySubwayExit(this.nearbySubwayExit)
                .build();
    }
}
