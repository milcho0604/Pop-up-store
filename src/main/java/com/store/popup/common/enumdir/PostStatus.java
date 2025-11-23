package com.store.popup.common.enumdir;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    UPCOMING("UPCOMING", "진행 예정"),
    ONGOING("ONGOING", "진행 중"),
    ENDED("ENDED", "종료");

    private final String key;
    private final String description;
}
