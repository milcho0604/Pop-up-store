package com.store.popup.comment.dto;

import com.store.popup.comment.domain.Comment;
import com.store.popup.pop.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReplyCommentSaveDto {
    private Long postId;
    private Long parentId;
    @NotEmpty(message = "content is essential")
    private String content;

    public Comment toEntity(Post post, Comment parent, String writerEmail, String nickName, String profileImg){
        return Comment.builder()
                .post(post)
                .memberEmail(writerEmail)
                .content(this.content)
                .nickName(nickName)
                .profileImg(profileImg)
                .parent(parent)
                .build();
    }
}
