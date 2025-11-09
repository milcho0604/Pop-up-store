package com.store.popup.information.repository;

import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {
    
    // 상태별 조회
    Page<Information> findByStatus(InformationStatus status, Pageable pageable);
    
    List<Information> findByStatus(InformationStatus status);
    
    // 특정 ID 리스트로 조회
    List<Information> findByIdIn(List<Long> ids);
    
    // 삭제되지 않은 정보만 조회
    Page<Information> findByDeletedAtIsNull(Pageable pageable);
    
    Page<Information> findByStatusAndDeletedAtIsNull(InformationStatus status, Pageable pageable);
}

