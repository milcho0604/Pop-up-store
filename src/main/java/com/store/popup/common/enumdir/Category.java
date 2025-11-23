package com.store.popup.common.enumdir;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    FASHION("FASHION", "패션"),
    BEAUTY("BEAUTY", "뷰티"),
    FOOD("FOOD", "푸드"),
    ART("ART", "아트/전시"),
    CHARACTER("CHARACTER", "캐릭터"),
    LIFESTYLE("LIFESTYLE", "라이프스타일"),
    SPORTS("SPORTS", "스포츠"),
    TECH("TECH", "테크/IT"),
    CULTURE("CULTURE", "문화/공연"),
    ETC("ETC", "기타");

    private final String key;
    private final String description;
}
