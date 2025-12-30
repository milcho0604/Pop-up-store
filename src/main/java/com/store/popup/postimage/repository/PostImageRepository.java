package com.store.popup.postimage.repository;

import com.store.popup.pop.domain.Post;
import com.store.popup.postimage.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // Post의 모든 이미지 조회 (순서대로)
    List<PostImage> findByPostAndDeletedAtIsNullOrderByDisplayOrderAsc(Post post);

    // Post의 이미지 개수
    Long countByPostAndDeletedAtIsNull(Post post);

    // PostImage 조회 (Post와 Member fetch join으로 N+1 방지)
    @Query("SELECT pi FROM PostImage pi " +
            "JOIN FETCH pi.post p " +
            "JOIN FETCH p.member " +
            "WHERE pi.id = :id")
    Optional<PostImage> findByIdWithPostAndMember(@Param("id") Long id);
}
