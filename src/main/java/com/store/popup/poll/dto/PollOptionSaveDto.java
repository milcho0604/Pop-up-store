package com.store.popup.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 투표 선택지 추가 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollOptionSaveDto {

    private Long postId;       // 투표 대상 Post ID
    private String description; // 선택지 설명 (선택사항)
}
