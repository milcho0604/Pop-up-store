package com.store.popup.postimage.repository;

import com.store.popup.pop.domain.Post;
import com.store.popup.postimage.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // Post의 모든 이미지 조회 (순서대로)
    List<PostImage> findByPostAndDeletedAtIsNullOrderByDisplayOrderAsc(Post post);

    // Post의 이미지 개수
    Long countByPostAndDeletedAtIsNull(Post post);
}
