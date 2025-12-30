package com.store.popup.qna.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Post별 Q&A 질문 엔티티
 */
@Entity
@Table(name = "question")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String content;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Answer answer;

    /**
     * 답변 여부 확인
     */
    public boolean hasAnswer() {
        return answer != null && answer.getDeletedAt() == null;
    }
}
