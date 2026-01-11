package com.store.popup.pop.domain;

import com.store.popup.common.domain.BaseTimeEntity;

import com.store.popup.tag.domain.Tag;
import com.store.popup.tag.dto.TagDto;
import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import com.store.popup.information.domain.Information;
import com.store.popup.member.domain.Address;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.dto.PostListDto;
import com.store.popup.pop.dto.PostUpdateReqDto;
import com.store.popup.postimage.domain.PostImage;
import com.store.popup.report.domain.Report;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // 평균 평점
    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    // 리뷰 개수
    @Column(name = "review_count")
    @Builder.Default
    private Long reviewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private PostStatus status = PostStatus.ONGOING;

    @Column
    private String phoneNumber;
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

    // 조회수
    private@Builder.Default
    Long viewCount = 0L;

    // 공유 횟수
    @Column(name = "share_count")
    @Builder.Default
    private Long shareCount = 0L;

    // 신고 목록
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Report> reportList = new ArrayList<>();

    // 태그
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    // 상세 영업 정보 (1:1 양방향)
    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PostDetail postDetail;

    // 이미지 갤러리 (1:N)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<PostImage> images = new ArrayList<>();

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
                .dong(this.address != null ? this.address.getDong() : null)
                .street(this.address != null ? this.address.getStreet() : null)
                .zipcode(this.address != null ? this.address.getZipcode() : null)
                .detailAddress(this.address != null ? this.address.getDetailAddress() : null)
                .category(this.category)
                .status(this.status)
                .tags(this.tags != null ?
                    this.tags.stream()
                        .map(TagDto::fromEntity)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public void updateImage(String postImgUrl){
        this.postImgUrl = postImgUrl;
    }

    public Post update(PostUpdateReqDto dto){
        // 제목
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        // 내용
        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }
        // 카테고리
        if (dto.getCategory() != null) {
            this.category = dto.getCategory();
        }
        // 전화번호
        if (dto.getPhoneNumber() != null) {  // null → 건드리지 않음. "" → 비우고 싶으면 별도 정책.
            this.phoneNumber = dto.getPhoneNumber();
        }
        // 시작일
        if (dto.getStartDate() != null) {
            this.startDate = dto.getStartDate();
        }
        // 종료일
        if (dto.getEndDate() != null) {
            this.endDate = dto.getEndDate();
        }
        // 주소 병합 (기존 address 유지 + 들어온 값만 반영)
        if (dto.getCity() != null || dto.getDong() != null || dto.getStreet() != null || dto.getZipcode() != null || dto.getDetailAddress() != null) {
            String currentCity = this.address != null ? this.address.getCity() : null;
            String currentDong = this.address != null ? this.address.getDong() : null;
            String currentStreet = this.address != null ? this.address.getStreet() : null;
            String currentZipcode = this.address != null ? this.address.getZipcode() : null;
            String currentDetailAddress = this.address != null ? this.address.getDetailAddress() : null;

            this.address = Address.builder()
                    .city(dto.getCity() != null ? dto.getCity() : currentCity)
                    .dong(dto.getDong() != null ? dto.getDong() : currentDong)
                    .street(dto.getStreet() != null ? dto.getStreet() : currentStreet)
                    .zipcode(dto.getZipcode() != null ? dto.getZipcode() : currentZipcode)
                    .detailAddress(dto.getDetailAddress() != null ? dto.getDetailAddress() : currentDetailAddress)
                    .build();
        }

        return this;
    }

    // 상태를 날짜 기준으로 자동 업데이트
    public void updateStatusByDate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.startDate != null && this.endDate != null) {
            if (now.isBefore(this.startDate)) {
                this.status = PostStatus.UPCOMING;
            } else if (now.isAfter(this.endDate)) {
                this.status = PostStatus.ENDED;
            } else {
                this.status = PostStatus.ONGOING;
            }
        }
    }

    // 태그 업데이트 메서드
    public void updateTags(List<Tag> newTags) {
        if (newTags != null) {
            this.tags.clear();
            this.tags.addAll(newTags);
        }
    }

    // 조회수 업데이트 메서드
    public void updateViewCount(Long viewCount) {
        this.viewCount = (viewCount != null) ? viewCount : 0L; // null이면 0L로 처리
    }

    // 좋아요 수 업데이트 메서드
    public void updateLikeCount(Long likeCount) {
        this.likeCount = (likeCount != null) ? likeCount : 0L; // null이면 0L로 처리
    }

    // 평균 평점 및 리뷰 개수 업데이트 메서드
    public void updateRatingInfo(Double averageRating, Long reviewCount) {
        this.averageRating = (averageRating != null) ? averageRating : 0.0;
        this.reviewCount = (reviewCount != null) ? reviewCount : 0L;
    }

    // 공유 횟수 증가
    public void incrementShareCount() {
        this.shareCount++;
    }

    // Information을 Post로 변환하는 static 메서드
    public static Post convertFromInformation(Information information, Member adminMember, String profileImgUrl) {
        Post post = Post.builder()
                .member(adminMember)
                .title(information.getTitle())
                .content(information.getContent())
                .postImgUrl(information.getPostImgUrl())
                .profileImgUrl(profileImgUrl)
                .startDate(information.getStartDate())
                .endDate(information.getEndDate())
                .address(information.getAddress())
                .phoneNumber(information.getPhoneNumber())
                .category(information.getCategory())
                .tags(new ArrayList<>(information.getTags()))
                .build();
        // 상태를 날짜 기준으로 자동 설정
        post.updateStatusByDate();
        return post;
    }

}
