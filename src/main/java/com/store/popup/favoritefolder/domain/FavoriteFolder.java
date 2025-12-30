package com.store.popup.favoritefolder.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.favorite.domain.Favorite;
import com.store.popup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 찜하기 폴더 엔티티
 */
@Entity
@Table(name = "favorite_folder")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FavoriteFolder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_folder_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String name;  // 폴더 이름 (예: "가고싶은곳", "갔다온곳")

    @Column(length = 200)
    private String description;  // 폴더 설명

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    /**
     * 폴더 이름 수정
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 폴더 설명 수정
     */
    public void updateDescription(String description) {
        this.description = description;
    }
}
