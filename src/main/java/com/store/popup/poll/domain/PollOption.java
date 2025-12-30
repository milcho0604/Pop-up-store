package com.store.popup.poll.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 투표 선택지 엔티티 (어떤 팝업 스토어를 선택할지)
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "poll_option")
public class PollOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnore
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;  // 투표 대상 팝업 스토어

    /**
     * 선택지 설명 (선택사항, 없으면 Post 제목 사용)
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 득표수
     */
    @Column(name = "vote_count")
    @Builder.Default
    private Long voteCount = 0L;

    /**
     * 득표수 증가
     */
    public void incrementVoteCount() {
        this.voteCount++;
    }

    /**
     * 득표수 감소 (투표 취소 시)
     */
    public void decrementVoteCount() {
        if (this.voteCount > 0) {
            this.voteCount--;
        }
    }

    /**
     * 선택지 설명 업데이트
     */
    public void updateDescription(String description) {
        this.description = description;
    }
}
