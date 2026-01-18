package com.store.popup.notification.dto;

import com.store.popup.notification.domain.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResDto {
    private Long id;
    private String memberEmail;
    private String title;
    private String content;
    private boolean isRead;
    private Type type;
    private Long refId;
    private String url;
    private LocalDateTime createdAt;
}
