package com.store.popup.pop.report.dto;

import com.store.popup.pop.report.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportListResDto {
    private Long id; //reportId
    private String reporterEmail; //신고자
    private String reportedEmail; //피신고자
    private String reason;
    private Long postId;
    private Long commentId;
    private Status status;
    private LocalDateTime createdTimeAt;
}
