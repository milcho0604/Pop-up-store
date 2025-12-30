package com.store.popup.poll.dto;

import com.store.popup.member.domain.Member;
import com.store.popup.poll.domain.Poll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 투표 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollSaveDto {

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean multipleChoice;

    public Poll toEntity(Member admin) {
        return Poll.builder()
                .member(admin)
                .title(this.title)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .multipleChoice(this.multipleChoice != null ? this.multipleChoice : false)
                .build();
    }
}
