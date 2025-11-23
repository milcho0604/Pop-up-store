package com.store.popup.pop.dto;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilterReqDto {

    // 검색 키워드 (제목, 내용, 위치에서 검색)
    private String keyword;

    // 카테고리 필터 (다중 선택 가능)
    private List<Category> categories;

    // 상태 필터 (다중 선택 가능)
    private List<PostStatus> statuses;

    // 지역 필터
    private String city;

    // 기간 필터 (해당 기간에 진행 중인 팝업)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    // 정렬 기준
    private SortType sortBy;

    public enum SortType {
        LATEST,        // 최신순
        POPULAR,       // 인기순 (좋아요 많은 순)
        VIEW_COUNT,    // 조회수 많은 순
        ENDING_SOON    // 마감 임박순
    }
}
