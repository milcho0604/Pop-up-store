package com.store.popup.pop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 요일별 영업시간 DTO
 * JSON으로 저장될 예정
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingHoursDto {
    private String open;   // 오픈 시간 (예: "09:00")
    private String close;  // 마감 시간 (예: "18:00")
    private Boolean closed; // 휴무 여부 (선택)
}
