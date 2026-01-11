package com.store.popup.pop.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import com.store.popup.pop.domain.Post;
import com.store.popup.tag.dto.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String dong;
    private String street;
    private String zipcode;

    // 상세 주소
    private String detailAddress;

    // 카테고리 및 상태
    private Category category;
    private PostStatus status;

    // 태그 목록
    private List<TagDto> tags;

    // 상세 영업 정보
    private PostDetailResDto businessInfo;

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
                .dong(post.getAddress() != null ? post.getAddress().getDong() : null)
                .street(post.getAddress() != null ? post.getAddress().getStreet() : null)
                .zipcode(post.getAddress() != null ? post.getAddress().getZipcode() : null)
                .detailAddress(post.getAddress() != null ? post.getAddress().getDetailAddress() : null)
                .category(post.getCategory())
                .status(post.getStatus())
                .tags(post.getTags() != null ?
                    post.getTags().stream()
                        .map(TagDto::fromEntity)
                        .collect(Collectors.toList()) : null)
                .businessInfo(post.getPostDetail() != null ?
                    PostDetailResDto.fromEntity(post.getPostDetail()) : null)
                .build();
    }

}

