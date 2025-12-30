package com.store.popup.poll.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 투표 엔티티 (예: 가장 가보고 싶은 팝업 스토어)
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "poll")
public class Poll extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;  // 작성자 (관리자)

    /**
     * 투표 제목 (예: "2025년 1월 가장 가보고 싶은 팝업 스토어")
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 투표 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 투표 시작일
     */
    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 투표 종료일
     */
    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * 복수 선택 가능 여부
     */
    @Column(name = "multiple_choice")
    @Builder.Default
    private Boolean multipleChoice = false;

    /**
     * 총 투표수
     */
    @Column(name = "total_votes")
    @Builder.Default
    private Long totalVotes = 0L;

    /**
     * 투표 선택지 목록
     */
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<PollOption> options = new ArrayList<>();

    /**
     * 투표 수정
     */
    public void update(String title, String description, LocalDateTime startDate,
                      LocalDateTime endDate, Boolean multipleChoice) {
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if (multipleChoice != null) {
            this.multipleChoice = multipleChoice;
        }
    }

    /**
     * 총 투표수 증가
     */
    public void incrementTotalVotes() {
        this.totalVotes++;
    }

    /**
     * 총 투표수 감소 (투표 취소 시)
     */
    public void decrementTotalVotes() {
        if (this.totalVotes > 0) {
            this.totalVotes--;
        }
    }

    /**
     * 현재 투표 진행중인지 확인
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate) && getDeletedAt() == null;
    }

    /**
     * 투표가 종료되었는지 확인
     */
    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }
}
