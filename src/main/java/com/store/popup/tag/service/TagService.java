package com.store.popup.tag.service;

import com.store.popup.tag.domain.Tag;
import com.store.popup.tag.dto.TagDto;
import com.store.popup.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /**
     * 태그 이름 리스트를 받아서 Tag 엔티티 리스트로 변환
     * 존재하지 않는 태그는 자동으로 생성
     */
    public List<Tag> findOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue; // 빈 태그는 건너뛰기
            }

            String normalizedName = normalizeTagName(tagName);

            Tag tag = tagRepository.findByName(normalizedName)
                    .orElseGet(() -> {
                        Tag newTag = Tag.builder()
                                .name(normalizedName)
                                .usageCount(0L)
                                .build();
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    /**
     * 태그 이름 정규화 (# 추가, 소문자 변환)
     */
    private String normalizeTagName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("태그 이름은 비어있을 수 없습니다.");
        }
        String normalized = name.trim();
        if (!normalized.startsWith("#")) {
            normalized = "#" + normalized;
        }
        return normalized.toLowerCase(); // 소문자 통일
    }

    /**
     * 모든 태그 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 인기 태그 목록 조회 (사용 횟수가 많은 순)
     */
    @Transactional(readOnly = true)
    public List<TagDto> getPopularTags(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "usageCount"));
        return tagRepository.findAll(pageRequest).stream()
                .map(TagDto::fromEntity)
                .collect(Collectors.toList());
    }
}

