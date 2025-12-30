package com.store.popup.pop.repository;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    List<Post> findByDeletedAtIsNull();
    // 사용자의 이메일을 기준으로 삭제되지 않은 게시글 목록 조회
    Page<Post> findByMemberAndDeletedAtIsNull(Member member, Pageable pageable);

    // 중복 체크: 주소와 운영 기간이 동일한 Post가 있는지 확인
    // 생성/변환 시 중복 검사
    boolean existsByAddress_CityAndAddress_StreetAndAddress_ZipcodeAndStartDateAndEndDateAndDeletedAtIsNull(
            String city, String street, String zipcode,
            LocalDateTime startDate, LocalDateTime endDate
    );

    // 수정 시 자기 자신 제외한 중복 검사
    boolean existsByIdNotAndAddress_CityAndAddress_StreetAndAddress_ZipcodeAndStartDateAndEndDateAndDeletedAtIsNull(
            Long id,
            String city, String street, String zipcode,
            LocalDateTime startDate, LocalDateTime endDate
    );

    // 중복 체크: 주소와 운영 기간이 동일한 Post가 있는지 확인
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL " +
           "AND p.address.city = :city " +
           "AND p.address.street = :street " +
           "AND p.address.zipcode = :zipcode " +
           "AND p.startDate = :startDate " +
           "AND p.endDate = :endDate")
    Optional<Post> findDuplicatePost(
            @Param("city") String city,
            @Param("street") String street,
            @Param("zipcode") String zipcode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // PostDetail을 함께 fetch join으로 조회
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.postDetail WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Post> findByIdWithPostDetail(@Param("id") Long id);

    // 기간별 게시글 수 (대시보드용)
    Long countByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    // 기간별 게시글 목록 (대시보드용)
    List<Post> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);
}
