package com.store.popup.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateReqDto {
    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime updateTime;
}
