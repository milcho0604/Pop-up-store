package com.store.popup.tag.dto;

import com.store.popup.tag.domain.Tag;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String name;  // "#한정판"
    private Long usageCount;  // 사용 횟수 (인기순 정렬용)

    public static TagDto fromEntity(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .usageCount(tag.getUsageCount())
                .build();
    }
}
