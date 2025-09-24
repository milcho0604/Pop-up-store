package com.store.popup.pop.report.dto;

import com.store.popup.pop.post.domain.Post;
import com.store.popup.pop.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportSaveReqDto {
    private String reason;
    private Long postId;
    private Long commentId;

//    public Report toEntity(Post post, Comment comment, String reporterEmail, String reportedEmail){
//        return Report.builder()
//                .reporterEmail(reporterEmail)
//                .reportedEmail(reportedEmail)
//                .reason(this.reason)
//                .post(post)
//                .comment(comment)
//                .status(Status.PROGRESS)
//                .build();
//    }
}
