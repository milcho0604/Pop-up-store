package com.store.popup.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공유 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareDto {

    private Long postId;
    private String shareUrl;
    private String title;
    private String description;
    private String imageUrl;
    private Long shareCount;
}
