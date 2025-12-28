package com.store.popup.notice.dto;

import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공지사항 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResDto {

    private Long noticeId;
    private String title;
    private String content;
    private NoticeType noticeType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private String authorName;      // 작성자 이름
    private String authorEmail;     // 작성자 이메일
    private boolean isActive;       // 현재 게시중인지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeResDto fromEntity(Notice notice) {
        return NoticeResDto.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .noticeType(notice.getNoticeType())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .viewCount(notice.getViewCount())
                .authorName(notice.getMember().getName())
                .authorEmail(notice.getMember().getMemberEmail())
                .isActive(notice.isActive())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
