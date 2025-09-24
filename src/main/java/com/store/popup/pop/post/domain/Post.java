package com.store.popup.pop.post.domain;

import com.store.popup.common.domain.BaseTimeEntity;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.post.dto.PostListDto;
import com.store.popup.pop.post.dto.PostUpdateReqDto;
import com.store.popup.pop.report.domain.Report;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
                .content(this.content)
                .likeCount(likeCount != null ? likeCount : 0)
                .viewCount(viewCount != null ? viewCount : 0)
                .postImgUrl(this.postImgUrl != null ? this.postImgUrl : null)
                .postImgUrl(this.postImgUrl)
                .createdTimeAt(this.getCreatedAt())
                .build();
    }

    public void updateImage(String postImgUrl){
        this.postImgUrl = postImgUrl;
    }

    public Post update(PostUpdateReqDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
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
