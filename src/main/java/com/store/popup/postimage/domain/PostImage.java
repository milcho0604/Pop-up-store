package com.store.popup.postimage.domain;

import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Post 이미지 엔티티 (갤러리용)
 */
@Entity
@Table(name = "post_image")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;  // 이미지 순서 (0부터 시작)

    @Column(length = 200)
    private String description;  // 이미지 설명 (선택)

    /**
     * 이미지 순서 변경
     */
    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
