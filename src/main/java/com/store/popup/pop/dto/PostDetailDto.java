package com.store.popup.pop.dto;

import com.store.popup.pop.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailDto {
    private Long id;
    private String memberEmail;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String title;
    private String content;
    private String profileImgUrl;
    private String postImgUrl;
    private Long likeCount;
    private Long viewCount;
    private LocalDateTime createdTimeAt;
    private LocalDateTime updatedTimeAt;
    
    // 팝업 스토어 운영 기간
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;

    public static PostDetailDto fromEntity(Post post, Long viewCount, Long likeCount){
        return PostDetailDto.builder()
                .id(post.getId())
                .memberEmail(post.getMember().getMemberEmail())
                .name(post.getMember().getName())
                .nickname(post.getMember().getNickname())
                .phoneNumber(post.getPhoneNumber())
                .title(post.getTitle())
                .content(post.getContent())
                .postImgUrl(post.getPostImgUrl())
                .profileImgUrl(post.getProfileImgUrl())
                .likeCount(likeCount != null ? likeCount : 0)
                .viewCount(viewCount != null ? viewCount : 0)
                .createdTimeAt(post.getCreatedAt())
                .updatedTimeAt(post.getUpdatedAt())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .city(post.getAddress() != null ? post.getAddress().getCity() : null)
                .street(post.getAddress() != null ? post.getAddress().getStreet() : null)
                .zipcode(post.getAddress() != null ? post.getAddress().getZipcode() : null)
                .build();
    }

}

