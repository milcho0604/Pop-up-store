package com.store.popup.notification.domain;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.store.popup.common.domain.BaseTimeEntity;
import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.notification.dto.NotificationResDto;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class FcmNotification extends BaseTimeEntity {

    private static final String BASE_URL = "http://localhost:8081/";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String recipient;

    private String title;

    private String content;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long refId;

    private String url;

    public static FcmNotification create(Member member, String title, String body, Type type, Long refId) {
        String url = buildUrl(member.getRole(), type, refId);

        return FcmNotification.builder()
                .member(member)
                .title(title)
                .content(body)
                .isRead(false)
                .type(type)
                .refId(refId)
                .recipient(member.getMemberEmail())
                .url(url)
                .build();
    }

    private static String buildUrl(Role role, Type type, Long refId) {
        if (refId == null || !Role.ADMIN.equals(role)) {
            return null;
        }

        String urlType = switch (type) {
            case REGISTER -> "admin/member/detail/" + refId;
            case REPORT_NOTIFICATION -> "admin/information/list/" + refId;
            case POST_NOTIFICATION -> "admin/information/detail/" + refId;
            default -> null;
        };

        return urlType != null ? BASE_URL + urlType : null;
    }

    public Message toFcmMessage(String token) {
        return Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(this.title)
                                .setBody(this.content)
                                .build())
                        .build())
                .putData("url", this.url != null ? this.url : "")
                .putData("notificationId", this.id != null ? String.valueOf(this.id) : "")
                .setToken(token)
                .build();
    }

    public void read() {
        this.isRead = true;
    }

    public NotificationResDto toResponseDto() {
        return NotificationResDto.builder()
                .id(this.id)
                .memberEmail(this.member.getMemberEmail())
                .title(this.title)
                .content(this.content)
                .isRead(this.isRead)
                .type(this.type)
                .refId(this.refId)
                .url(this.url)
                .createdAt(this.getCreatedAt())
                .build();
    }

    /**
     * @deprecated Use {@link #toResponseDto()} instead
     */
    @Deprecated
    public NotificationResDto listFromEntity() {
        return toResponseDto();
    }
}
