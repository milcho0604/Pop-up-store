package com.store.popup.qna.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Q&A 답변 엔티티 (운영자 답변)
 */
@Entity
@Table(name = "answer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 답변 작성자 (게시글 작성자 또는 관리자)

    @Column(nullable = false, length = 1000)
    private String content;

    /**
     * 답변 내용 수정
     */
    public void updateContent(String content) {
        this.content = content;
    }
}
