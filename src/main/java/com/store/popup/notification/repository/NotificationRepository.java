package com.store.popup.notification.repository;

import com.store.popup.notification.domain.FcmNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<FcmNotification, Long> {
    //    Page<FcmNotification> findByMemberAndCreatedAtAfter(Member member, LocalDateTime dateTime, Pageable pageable);
    Page<FcmNotification> findByMemberIdAndCreatedAtAfter(Long memberId, LocalDateTime dateTime, Pageable pageable);

}
