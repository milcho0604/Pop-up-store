package com.store.popup.history.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import com.store.popup.history.domain.ViewHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 조회 히스토리 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistoryDto {

    private Long historyId;
    private Long postId;
    private String postTitle;
    private String postImgUrl;
    private Category category;
    private PostStatus status;
    private LocalDateTime viewedAt;  // 마지막 조회 시간

    public static ViewHistoryDto fromEntity(ViewHistory viewHistory) {
        return ViewHistoryDto.builder()
                .historyId(viewHistory.getId())
                .postId(viewHistory.getPost().getId())
                .postTitle(viewHistory.getPost().getTitle())
                .postImgUrl(viewHistory.getPost().getPostImgUrl())
                .category(viewHistory.getPost().getCategory())
                .status(viewHistory.getPost().getStatus())
                .viewedAt(viewHistory.getUpdatedAt())
                .build();
    }
}
