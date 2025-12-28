package com.store.popup.notice.dto;

import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.member.domain.Member;
import com.store.popup.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공지사항 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSaveDto {

    private String title;
    private String content;
    private NoticeType noticeType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Notice toEntity(Member admin) {
        return Notice.builder()
                .member(admin)
                .title(this.title)
                .content(this.content)
                .noticeType(this.noticeType)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .build();
    }
}
