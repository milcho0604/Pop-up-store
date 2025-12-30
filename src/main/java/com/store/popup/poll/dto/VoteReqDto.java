package com.store.popup.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 투표하기 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteReqDto {

    private List<Long> optionIds;  // 선택한 옵션 ID 목록 (복수 선택 가능)
}
