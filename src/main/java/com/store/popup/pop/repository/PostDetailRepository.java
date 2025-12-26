package com.store.popup.pop.repository;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.domain.PostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {

    Optional<PostDetail> findByPost(Post post);

    Optional<PostDetail> findByPostId(Long postId);

    boolean existsByPostId(Long postId);
}
