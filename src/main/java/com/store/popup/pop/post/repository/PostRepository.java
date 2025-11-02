package com.store.popup.pop.post.repository;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDeletedAtIsNull();
    // 사용자의 이메일을 기준으로 삭제되지 않은 게시글 목록 조회
    Page<Post> findByMemberAndDeletedAtIsNull(Member member, Pageable pageable);
    
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
}
