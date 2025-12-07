package com.store.popup.comment.domain;

import com.store.popup.comment.dto.CommentDetailDto;
import com.store.popup.comment.dto.CommentUpdateReqDto;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.pop.domain.Post;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(nullable = false)
    private String memberEmail; //post 작성자 / 의사
    @Column(nullable = false, length = 3000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    //댓글 대댓글, 대대댓글... 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    private String nickName;
    private String profileImg;

    public CommentDetailDto listFromEntity(){
        return CommentDetailDto.builder()
                .id(this.id)
                .doctorEmail(this.memberEmail)
                .content(this.content)
                .createdTimeAt(this.getCreatedAt())
                .updatedTimeAt(this.getUpdatedAt())
                .build();
    }

    public Comment update(CommentUpdateReqDto dto){
        this.content = dto.getContent();
        return this;
    }
}
