package com.store.popup.member.repository;


import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 닉네임 중복 검사
    boolean existsByNickname(String nickname);
    // 이메일 중복 검사
    boolean existsByMemberEmail(String memberEmail);
    Optional<Member> findByMemberEmail(String memberEmail);

    Optional<Member> findByIdAndMemberEmail(Long id, String memberEmail);

    Page<Member> findAll(Pageable pageable);
    Page<Member> findByRoleAndDeletedAtIsNull(Role role, Pageable pageable);
    //병원별 삭제되지 않은 의사목록
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    default Member findByIdOrThrow(Long id){
        return findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 member를 찾을 수 없습니다."));
    }

    // 탈퇴하지 않은 멤버 이메일찾기
    Optional<Member> findByMemberEmailAndDeletedAtIsNull(String memberEmail);

    default Member findByMemberEmailOrThrow(String memberEmail){
        return findByMemberEmailAndDeletedAtIsNull(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("email에 해당하는 회원을 찾을 수 없습니다."));
    }

    List<Member> findAllByNoShowCountGreaterThanEqualAndDeletedAtIsNull(int noShowCount);

    @Query("SELECT m FROM Member m WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.memberEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND m.role = 'Member'")
    List<Member> searchMembers(@Param("keyword") String keyword);

    Page<Member> findByNameContainingOrMemberEmailContaining(String name, String memberEmail, Pageable pageable);
    Page<Member> findByIsVerifiedAndDeletedAtIsNotNullAndRole(boolean isVerified, Role role, Pageable pageable);

    Page<Member> findByIsVerifiedAndDeletedAtIsNullAndRole(boolean isVerified, Role role, Pageable pageable);


    // 정상 회원 중 인증 여부 필터링
    Page<Member> findByIsVerifiedAndDeletedAtIsNull(boolean isVerified, Pageable pageable);

    // 탈퇴 회원 중 인증 여부 필터링
    Page<Member> findByIsVerifiedAndDeletedAtIsNotNull(boolean isVerified, Pageable pageable);

    // 기간별 신규 가입자 수 (대시보드용)
    Long countByCreatedAtBetweenAndDeletedAtIsNull(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

}
