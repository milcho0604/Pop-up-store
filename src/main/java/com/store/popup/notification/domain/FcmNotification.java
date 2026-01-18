package com.store.popup.notification.domain;

import com.store.popup.common.domain.BaseTimeEntity;
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

    private Long refId; //post, comment id 저장하는데

    public void read() {
        this.isRead = true;
    }

    private String url;

    public NotificationResDto listFromEntity() {
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
}
