package com.store.popup.pop.post.dto;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailDto {
    private Long id;
    private String memberEmail;
    private String name;
    private String title;
    private String content;
    private String profileImgUrl;
    private String postImgUrl;
    private Long likeCount;
    private Long viewCount;
    private LocalDateTime createdTimeAt;
    private LocalDateTime updatedTimeAt;

    public static PostDetailDto fromEntity(Post post, Long viewCount, Long likeCount){
        return PostDetailDto.builder()
                .id(post.getId())
                .memberEmail(post.getMember().getMemberEmail())
                .name(post.getMember().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .postImgUrl(post.getPostImgUrl())
                .profileImgUrl(post.getProfileImgUrl())
                .likeCount(likeCount != null ? likeCount : 0)
                .viewCount(viewCount != null ? viewCount : 0)
                .createdTimeAt(post.getCreatedAt())
                .updatedTimeAt(post.getUpdatedAt())
                .build();
    }

}

