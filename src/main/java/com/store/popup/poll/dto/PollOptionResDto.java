package com.store.popup.poll.dto;

import com.store.popup.poll.domain.PollOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 투표 선택지 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollOptionResDto {

    private Long optionId;
    private Long postId;
    private String postTitle;
    private String postImgUrl;
    private String description;
    private Long voteCount;
    private Double votePercentage;  // 득표율

    public static PollOptionResDto fromEntity(PollOption option, Long totalVotes) {
        double percentage = 0.0;
        if (totalVotes > 0) {
            percentage = (option.getVoteCount() * 100.0) / totalVotes;
        }

        return PollOptionResDto.builder()
                .optionId(option.getId())
                .postId(option.getPost().getId())
                .postTitle(option.getPost().getTitle())
                .postImgUrl(option.getPost().getPostImgUrl())
                .description(option.getDescription())
                .voteCount(option.getVoteCount())
                .votePercentage(Math.round(percentage * 10.0) / 10.0)  // 소수점 1자리
                .build();
    }
}
