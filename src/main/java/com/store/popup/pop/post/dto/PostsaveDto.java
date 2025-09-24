package com.store.popup.pop.post.dto;

import com.store.popup.pop.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsaveDto {

    @NotEmpty(message = "email is essential")
    private String memberEmail;

    private String memberName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private MultipartFile postImage;

    private String postImgUrl;

    private String profileImgUrl;



    public Post toEntity(String postImgUrl, String memberEmail, String memberName, String profileImgUrl) {
        return Post.builder()
                .memberEmail(memberEmail)
                .memberName(memberName)
                .title(this.title)
                .content(this.content)
                .postImgUrl(postImgUrl)
                .profileImgUrl(profileImgUrl)
                .build();
    }
}
