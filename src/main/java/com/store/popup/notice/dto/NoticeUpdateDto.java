package com.store.popup.notice.dto;

import com.store.popup.common.enumdir.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공지사항 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateDto {

    private String title;
    private String content;
    private NoticeType noticeType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
