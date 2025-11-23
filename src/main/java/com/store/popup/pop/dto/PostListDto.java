package com.store.popup.pop.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListDto {
    private Long id;
    private String memberEmail;
    private String memberNickname;
    private String title;
    private String content;
    private Long likeCount;
    private Long viewCount;
    private LocalDateTime createdTimeAt;
    private String postImgUrl;
    
    // 팝업 스토어 운영 기간
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;

    // 상세 주소
    private String detailAddress;

    // 카테고리 및 상태
    private Category category;
    private PostStatus status;
}
