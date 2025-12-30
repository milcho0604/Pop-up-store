package com.store.popup.poll.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 투표 기록 엔티티 (누가 어떤 선택지에 투표했는지)
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "poll_vote",
       uniqueConstraints = @UniqueConstraint(columnNames = {"poll_id", "member_id", "poll_option_id"}))
public class PollVote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnore
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_option_id", nullable = false)
    @JsonIgnore
    private PollOption pollOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;
}
