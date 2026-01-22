package com.store.popup.notification.dto;

import com.store.popup.notification.domain.Type;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendFcmReqDto {
    private String memberEmail;
    private String title;
    private String content;
    private Type type;
    private Long refId;
}
