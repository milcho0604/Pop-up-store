package com.store.popup.postimage.dto;

import com.store.popup.postimage.domain.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Post 이미지 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageDto {

    private Long imageId;
    private String imageUrl;
    private Integer displayOrder;
    private String description;

    public static PostImageDto fromEntity(PostImage postImage) {
        return PostImageDto.builder()
                .imageId(postImage.getId())
                .imageUrl(postImage.getImageUrl())
                .displayOrder(postImage.getDisplayOrder())
                .description(postImage.getDescription())
                .build();
    }
}
