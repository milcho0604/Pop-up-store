package com.store.popup.common.enumdir;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayOfHoliday {
    Monday("MONDAY","월요일"),
    Tuesday("TUESDAY","화요일"),
    Wednesday("WEDNESDAY","수요일"),
    Thursday("THURSDAY","목요일"),
    Friday("FRIDAY","금요일"),
    Saturday("SATURDAY","토요일"),
    Sunday("SUNDAY","일요일");

    private final String key;
    private final String value;
}