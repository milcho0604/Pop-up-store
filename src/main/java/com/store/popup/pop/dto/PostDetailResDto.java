package com.store.popup.pop.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.popup.pop.domain.PostDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * PostDetail 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class PostDetailResDto {

    private Long postDetailId;
    private Long postId;
    private Map<String, OperatingHoursDto> operatingHours; // JSON에서 파싱된 요일별 영업시간
    private String dayOff;                                  // 휴무일 정보
    private String entryFee;                                // 입장료 정보
    private Boolean parkingAvailable;                       // 주차 가능 여부
    private String parkingFee;                              // 주차 요금
    private String nearbySubway;                            // 가까운 지하철역
    private String nearbySubwayExit;                        // 가까운 지하철 출구
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static PostDetailResDto fromEntity(PostDetail postDetail) {
        Map<String, OperatingHoursDto> operatingHoursMap = null;

        // JSON 문자열을 Map으로 파싱
        if (postDetail.getOperatingHoursJson() != null && !postDetail.getOperatingHoursJson().isEmpty()) {
            try {
                operatingHoursMap = objectMapper.readValue(
                        postDetail.getOperatingHoursJson(),
                        new TypeReference<Map<String, OperatingHoursDto>>() {}
                );
            } catch (JsonProcessingException e) {
                log.error("영업시간 JSON 파싱 실패: {}", e.getMessage());
            }
        }

        return PostDetailResDto.builder()
                .postDetailId(postDetail.getId())
                .postId(postDetail.getPost().getId())
                .operatingHours(operatingHoursMap)
                .dayOff(postDetail.getDayOff())
                .entryFee(postDetail.getEntryFee())
                .parkingAvailable(postDetail.getParkingAvailable())
                .parkingFee(postDetail.getParkingFee())
                .nearbySubway(postDetail.getNearbySubway())
                .nearbySubwayExit(postDetail.getNearbySubwayExit())
                .createdAt(postDetail.getCreatedAt())
                .updatedAt(postDetail.getUpdatedAt())
                .build();
    }
}
