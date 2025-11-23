package com.store.popup.pop.repository;

import com.store.popup.common.enumdir.Category;
import com.store.popup.common.enumdir.PostStatus;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.SearchFilterReqDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> searchWithFilters(SearchFilterReqDto searchFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 삭제되지 않은 게시글만 조회
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // 키워드 검색 (제목, 내용, 주소에서 검색)
            if (searchFilter.getKeyword() != null && !searchFilter.getKeyword().trim().isEmpty()) {
                String keyword = "%" + searchFilter.getKeyword().trim() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), keyword);
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), keyword);
                Predicate cityPredicate = criteriaBuilder.like(root.get("address").get("city"), keyword);
                Predicate streetPredicate = criteriaBuilder.like(root.get("address").get("street"), keyword);

                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate, cityPredicate, streetPredicate));
            }

            // 카테고리 필터
            if (searchFilter.getCategories() != null && !searchFilter.getCategories().isEmpty()) {
                predicates.add(root.get("category").in(searchFilter.getCategories()));
            }

            // 상태 필터
            if (searchFilter.getStatuses() != null && !searchFilter.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(searchFilter.getStatuses()));
            }

            // 지역 필터
            if (searchFilter.getCity() != null && !searchFilter.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("address").get("city"), searchFilter.getCity().trim()));
            }

            // 기간 필터 (해당 기간과 겹치는 팝업 검색)
            if (searchFilter.getStartDate() != null && searchFilter.getEndDate() != null) {
                // 검색 기간과 팝업 운영 기간이 겹치는 경우
                // (팝업 시작일 <= 검색 종료일) AND (팝업 종료일 >= 검색 시작일)
                Predicate startDatePredicate = criteriaBuilder.lessThanOrEqualTo(
                        root.get("startDate"), searchFilter.getEndDate());
                Predicate endDatePredicate = criteriaBuilder.greaterThanOrEqualTo(
                        root.get("endDate"), searchFilter.getStartDate());
                predicates.add(criteriaBuilder.and(startDatePredicate, endDatePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
