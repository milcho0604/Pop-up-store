package com.store.popup.pop.repository;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.domain.PostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {

    Optional<PostDetail> findByPost(Post post);

    Optional<PostDetail> findByPostId(Long postId);

    boolean existsByPostId(Long postId);

    // Post를 함께 fetch join으로 조회 (LAZY 로딩 에러 방지)
    @Query("SELECT pd FROM PostDetail pd JOIN FETCH pd.post WHERE pd.post.id = :postId AND pd.deletedAt IS NULL")
    Optional<PostDetail> findByPostIdWithPost(@Param("postId") Long postId);
}
