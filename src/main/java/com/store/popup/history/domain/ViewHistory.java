package com.store.popup.history.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 최근 본 팝업 히스토리
 */
@Entity
@Table(name = "view_history", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "post_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ViewHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 조회 시간 갱신
     */
    public void refreshViewTime() {
        this.touch();  // updatedAt 갱신
    }
}
