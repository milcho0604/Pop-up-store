package com.store.popup.pop.post.dto;

import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveDto {

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

    // 내부적으로 사용할 LocalDateTime 필드들
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    private String city;
    private String street;
    private String zipcode;

    public Post toEntity(String postImgUrl, Member member, String profileImgUrl) {
        
        Address address = null;
        if (city != null || street != null || zipcode != null) {
            address = Address.builder()
                    .city(city)
                    .street(street)
                    .zipcode(zipcode)
                    .build();
        }
        
        return Post.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .postImgUrl(postImgUrl)
                .profileImgUrl(profileImgUrl)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .address(address)
                .build();
    }
}
