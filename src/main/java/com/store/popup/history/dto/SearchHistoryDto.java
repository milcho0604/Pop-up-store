package com.store.popup.history.dto;

import com.store.popup.history.domain.SearchHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 검색 기록 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryDto {

    private Long historyId;
    private String keyword;
    private LocalDateTime searchedAt;

    public static SearchHistoryDto fromEntity(SearchHistory searchHistory) {
        return SearchHistoryDto.builder()
                .historyId(searchHistory.getId())
                .keyword(searchHistory.getKeyword())
                .searchedAt(searchHistory.getCreatedAt())
                .build();
    }
}
