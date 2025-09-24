package com.store.popup.common.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // deletedTimeAt을 설정하는 메서드를 추가(기존 코드에는 deletedTImeAt이 생성시에 들어감)
    public void setDeletedTimeAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public void updateDeleteAt(){
        this.deletedAt = LocalDateTime.now();
    }

    public void acceptHospitalAdmin(){
        this.deletedAt = null;
    }
}
