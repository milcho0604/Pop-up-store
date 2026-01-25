package com.store.popup.notification.repository;

import com.store.popup.notification.domain.FcmNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<FcmNotification, Long> {

    Page<FcmNotification> findByMemberIdAndCreatedAtAfter(Long memberId, LocalDateTime dateTime, Pageable pageable);

    Page<FcmNotification> findByMemberIdAndReadFalseAndCreatedAtAfter(Long memberId, LocalDateTime dateTime, Pageable pageable);

    @Modifying
    @Query("UPDATE FcmNotification n SET n.isRead = true WHERE n.id IN :ids AND n.member.id = :memberId")
    int markAsReadByIds(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE FcmNotification n SET n.isRead = true WHERE n.member.id = :memberId AND n.isRead = false")
    int markAllAsRead(@Param("memberId") Long memberId);

    long countByMemberIdAndCreatedAtAfter(Long memberId, LocalDateTime dateTime);

    long countByMemberIdAndIsReadFalseAndCreatedAtAfter(Long memberId, LocalDateTime dateTime);
}
