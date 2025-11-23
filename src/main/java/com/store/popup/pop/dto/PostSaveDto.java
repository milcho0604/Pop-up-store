package com.store.popup.pop.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveDto {

    @NotEmpty(message = "email is essential")
    private String memberEmail;

    private String memberName;

    private String phoneNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private MultipartFile postImage;

    private String postImgUrl;

    private String profileImgUrl;

    // 내부적으로 사용할 LocalDateTime 필드들
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;
    
    // 상세 주소
    private String detailAddress;

    // 카테고리
    private Category category;

    public Post toEntity(String postImgUrl, Member member, String profileImgUrl) {

        Address address = null;
        if (city != null || street != null || zipcode != null || detailAddress != null) {
            address = Address.builder()
                    .city(city)
                    .street(street)
                    .zipcode(zipcode)
                    .detailAddress(detailAddress)
                    .build();
        }

        Post post = Post.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .postImgUrl(postImgUrl)
                .profileImgUrl(profileImgUrl)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .address(address)
                .phoneNumber(this.phoneNumber)
                .category(this.category)
                .build();

        // 상태를 날짜 기준으로 자동 설정
        post.updateStatusByDate();
        return post;
    }
}
