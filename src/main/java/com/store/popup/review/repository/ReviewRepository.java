package com.store.popup.review.repository;

import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import com.store.popup.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Post의 모든 리뷰 조회 (페이징)
    Page<Review> findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(Post post, Pageable pageable);

    // Post의 모든 리뷰 조회 (리스트)
    List<Review> findByPostAndDeletedAtIsNullOrderByCreatedAtDesc(Post post);

    // Member의 모든 리뷰 조회 (페이징)
    Page<Review> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member, Pageable pageable);

    // Member의 모든 리뷰 조회 (리스트)
    List<Review> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member);

    // 특정 Post에 대한 특정 Member의 리뷰 존재 여부
    boolean existsByPostAndMemberAndDeletedAtIsNull(Post post, Member member);

    // 특정 Post에 대한 특정 Member의 리뷰 조회
    Optional<Review> findByPostAndMemberAndDeletedAtIsNull(Post post, Member member);

    // Post의 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    Double calculateAverageRating(@Param("postId") Long postId);

    // Post의 리뷰 개수
    @Query("SELECT COUNT(r) FROM Review r WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    Long countByPostId(@Param("postId") Long postId);

    // Post의 평가 항목별 평균 계산
    @Query("SELECT AVG(r.satisfaction) FROM Review r WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    Double calculateAverageSatisfaction(@Param("postId") Long postId);

    @Query("SELECT AVG(r.waitingTime) FROM Review r WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    Double calculateAverageWaitingTime(@Param("postId") Long postId);

    @Query("SELECT AVG(r.photoAvailability) FROM Review r WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    Double calculateAveragePhotoAvailability(@Param("postId") Long postId);
}
