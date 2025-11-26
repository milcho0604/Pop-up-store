package com.store.popup.tag.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // "#한정판", "#무료입장" 등

    @Column
    private String description;  // 태그 설명 (선택)

    @Column
    @Builder.Default
    private Long usageCount = 0L;  // 사용 횟수 (통계용)
}
