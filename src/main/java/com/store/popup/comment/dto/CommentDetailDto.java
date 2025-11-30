package com.store.popup.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDetailDto {
    private Long id;
    private String nickName;
    private String doctorEmail;
    private String content;
    private String profileImg;
    private LocalDateTime createdTimeAt;
    private LocalDateTime updatedTimeAt;
    private Long PostId;
    private Long parentId;
}
