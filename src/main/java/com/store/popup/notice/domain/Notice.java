package com.store.popup.notice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "notice")
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;  // 작성자 (관리자)

    /**
     * 공지사항 제목
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 공지사항 내용
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 공지사항 타입 (NORMAL, IMPORTANT, POPUP)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", nullable = false)
    private NoticeType noticeType;

    /**
     * 게시 시작일
     */
    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 게시 종료일
     */
    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * 조회수
     */
    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * 공지사항 수정
     */
    public void update(String title, String content, NoticeType noticeType,
                      LocalDateTime startDate, LocalDateTime endDate) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (noticeType != null) {
            this.noticeType = noticeType;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 현재 게시중인지 확인
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate) && getDeletedAt() == null;
    }
}
