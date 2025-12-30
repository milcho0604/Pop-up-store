package com.store.popup.poll.dto;

import com.store.popup.poll.domain.Poll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 투표 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollResDto {

    private Long pollId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean multipleChoice;
    private Long totalVotes;
    private boolean isActive;
    private boolean isEnded;
    private List<PollOptionResDto> options;  // 선택지 목록
    private LocalDateTime createdAt;

    public static PollResDto fromEntity(Poll poll) {
        List<PollOptionResDto> optionDtos = poll.getOptions().stream()
                .filter(option -> option.getDeletedAt() == null)
                .map(option -> PollOptionResDto.fromEntity(option, poll.getTotalVotes()))
                .collect(Collectors.toList());

        return PollResDto.builder()
                .pollId(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .startDate(poll.getStartDate())
                .endDate(poll.getEndDate())
                .multipleChoice(poll.getMultipleChoice())
                .totalVotes(poll.getTotalVotes())
                .isActive(poll.isActive())
                .isEnded(poll.isEnded())
                .options(optionDtos)
                .createdAt(poll.getCreatedAt())
                .build();
    }
}
