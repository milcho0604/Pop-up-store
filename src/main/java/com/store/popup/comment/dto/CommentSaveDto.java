package com.store.popup.comment.dto;

import com.store.popup.comment.domain.Comment;
import com.store.popup.pop.domain.Post;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSaveDto {
    @NotEmpty(message = "postId is essential")
    //    게시글 ID
    private Long postId;
    //    댓글 내용
    @NotEmpty(message = "content is essential")
    private String content;

    public Comment toEntity(Post post, Comment parent, String writerEmail, String name, String profileImg){
        return Comment.builder()
                .post(post)
                .doctorEmail(writerEmail)
                .content(this.content)
                .name(name)
                .profileImg(profileImg)
                .parent(parent)
                .build();
    }
}
