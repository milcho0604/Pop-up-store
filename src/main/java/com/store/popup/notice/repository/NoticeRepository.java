package com.store.popup.notice.repository;

import com.store.popup.common.enumdir.NoticeType;
import com.store.popup.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 모든 공지사항 조회 (페이징)
    Page<Notice> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    // 현재 게시중인 공지사항 조회 (페이징)
    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND n.startDate <= :now AND n.endDate >= :now " +
           "ORDER BY n.createdAt DESC")
    Page<Notice> findActiveNotices(@Param("now") LocalDateTime now, Pageable pageable);

    // 현재 게시중인 공지사항 조회 (리스트)
    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND n.startDate <= :now AND n.endDate >= :now " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findActiveNoticesList(@Param("now") LocalDateTime now);

    // 현재 게시중인 팝업 공지사항 조회
    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND n.noticeType = 'POPUP' " +
           "AND n.startDate <= :now AND n.endDate >= :now " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findActivePopupNotices(@Param("now") LocalDateTime now);

    // 타입별 공지사항 조회 (페이징)
    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND n.noticeType = :noticeType " +
           "AND n.startDate <= :now AND n.endDate >= :now " +
           "ORDER BY n.createdAt DESC")
    Page<Notice> findActiveNoticesByType(
            @Param("noticeType") NoticeType noticeType,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    // 타입별 공지사항 조회 (리스트)
    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND n.noticeType = :noticeType " +
           "AND n.startDate <= :now AND n.endDate >= :now " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findActiveNoticesByTypeList(
            @Param("noticeType") NoticeType noticeType,
            @Param("now") LocalDateTime now
    );
}
