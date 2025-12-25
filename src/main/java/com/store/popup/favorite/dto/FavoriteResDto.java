package com.store.popup.favorite.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.favorite.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResDto {
    private Long favoriteId;
    private Long postId;
    private String postTitle;
    private String postContent;
    private String postImgUrl;
    private Category category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String city;
    private String street;
    private String zipcode;
    private LocalDateTime favoritedAt;

    public static FavoriteResDto fromEntity(Favorite favorite) {
        return FavoriteResDto.builder()
                .favoriteId(favorite.getId())
                .postId(favorite.getPost().getId())
                .postTitle(favorite.getPost().getTitle())
                .postContent(favorite.getPost().getContent())
                .postImgUrl(favorite.getPost().getPostImgUrl())
                .category(favorite.getPost().getCategory())
                .startDate(favorite.getPost().getStartDate())
                .endDate(favorite.getPost().getEndDate())
                .city(favorite.getPost().getAddress() != null ? favorite.getPost().getAddress().getCity() : null)
                .street(favorite.getPost().getAddress() != null ? favorite.getPost().getAddress().getStreet() : null)
                .zipcode(favorite.getPost().getAddress() != null ? favorite.getPost().getAddress().getZipcode() : null)
                .favoritedAt(favorite.getCreatedAt())
                .build();
    }
}
