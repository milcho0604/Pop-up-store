package com.store.popup.pop.post.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.store.popup.common.domain.BaseTimeEntity;

import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.post.dto.PostListDto;
import com.store.popup.pop.post.dto.PostUpdateReqDto;
import com.store.popup.pop.report.domain.Report;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 3000)
    private String content;
    @Column
    private String postImgUrl;
    @Builder.Default
    private Long likeCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String profileImgUrl;

    // 팝업 스토어 운영 기간
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    // 팝업 스토어 주소
    @Embedded
    private Address address;


    private@Builder.Default
    Long viewCount = 0L;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Report> reportList = new ArrayList<>();

    public PostListDto listFromEntity(Long viewCount, Long likeCount){
        return PostListDto.builder()
                .id(this.id)
                .title(this.title)
                .memberEmail(this.member.getMemberEmail())
                .memberNickname(this.member.getNickname())
                .content(this.content)
                .likeCount(likeCount != null ? likeCount : 0)
                .viewCount(viewCount != null ? viewCount : 0)
                .postImgUrl(this.postImgUrl)
                .createdTimeAt(this.getCreatedAt())
                .startDate(this.startDate)
                .endDate(this.endDate)
                .city(this.address != null ? this.address.getCity() : null)
                .street(this.address != null ? this.address.getStreet() : null)
                .zipcode(this.address != null ? this.address.getZipcode() : null)
                .build();
    }

    public void updateImage(String postImgUrl){
        this.postImgUrl = postImgUrl;
    }

    public Post update(PostUpdateReqDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        
        // 시작일과 마감일 업데이트
        if (dto.getStartDate() != null) {
            this.startDate = dto.getStartDate();
        }
        if (dto.getEndDate() != null) {
            this.endDate = dto.getEndDate();
        }
        
        // 주소 업데이트
        if (dto.getCity() != null || dto.getStreet() != null || dto.getZipcode() != null) {
            String currentCity = this.address != null ? this.address.getCity() : null;
            String currentStreet = this.address != null ? this.address.getStreet() : null;
            String currentZipcode = this.address != null ? this.address.getZipcode() : null;
            
            this.address = Address.builder()
                    .city(dto.getCity() != null ? dto.getCity() : currentCity)
                    .street(dto.getStreet() != null ? dto.getStreet() : currentStreet)
                    .zipcode(dto.getZipcode() != null ? dto.getZipcode() : currentZipcode)
                    .build();
        }
        
        return this;
    }

    // 조회수 업데이트 메서드
    public void updateViewCount(Long viewCount) {
        this.viewCount = (viewCount != null) ? viewCount : 0L; // null이면 0L로 처리
    }

    // 좋아요 수 업데이트 메서드
    public void updateLikeCount(Long likeCount) {
        this.likeCount = (likeCount != null) ? likeCount : 0L; // null이면 0L로 처리
    }

}
