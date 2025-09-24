package com.store.popup.pop.post.repository;

import com.store.popup.pop.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDeletedTimeAtIsNull();

    // 사용자의 이메일을 기준으로 삭제되지 않은 게시글 목록 조회
    Page<Post> findByMemberEmailAndDeletedTimeAtIsNull(String memberEmail, Pageable pageable);
}
