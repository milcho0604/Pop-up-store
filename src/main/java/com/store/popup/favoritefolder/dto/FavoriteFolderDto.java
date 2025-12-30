package com.store.popup.favoritefolder.dto;

import com.store.popup.favoritefolder.domain.FavoriteFolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 찜하기 폴더 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteFolderDto {

    private Long folderId;
    private String name;
    private String description;
    private Long favoriteCount;  // 폴더 내 찜 개수
    private LocalDateTime createdAt;

    public static FavoriteFolderDto fromEntity(FavoriteFolder folder) {
        long favoriteCount = folder.getFavorites().stream()
                .filter(fav -> fav.getDeletedAt() == null)
                .count();

        return FavoriteFolderDto.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .favoriteCount(favoriteCount)
                .createdAt(folder.getCreatedAt())
                .build();
    }
}
