package com.store.popup.favorite.repository;

import com.store.popup.favorite.domain.Favorite;
import com.store.popup.favoritefolder.domain.FavoriteFolder;
import com.store.popup.member.domain.Member;
import com.store.popup.pop.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Member와 Post로 찜 조회
    Optional<Favorite> findByMemberAndPost(Member member, Post post);

    // Member와 Post로 찜 존재 여부 확인
    boolean existsByMemberAndPost(Member member, Post post);

    // Member의 모든 찜 조회 (페이징)
    Page<Favorite> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member, Pageable pageable);

    // Member의 모든 찜 조회 (리스트)
    List<Favorite> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member);

    // Member의 찜 개수
    long countByMemberAndDeletedAtIsNull(Member member);

    // Post의 찜 개수
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.post.id = :postId AND f.deletedAt IS NULL")
    long countByPostId(@Param("postId") Long postId);

    // 폴더별 찜 조회
    List<Favorite> findByFolderAndDeletedAtIsNull(FavoriteFolder folder);

    // 폴더 내 찜 조회 (페이징)
    Page<Favorite> findByMemberAndFolderAndDeletedAtIsNullOrderByCreatedAtDesc(Member member, FavoriteFolder folder, Pageable pageable);

    // 기본 폴더(folder=null) 찜 조회
    List<Favorite> findByMemberAndFolderIsNullAndDeletedAtIsNullOrderByCreatedAtDesc(Member member);

    // 폴더 내 모든 찜을 기본 폴더로 일괄 이동 (벌크 업데이트)
    @Modifying
    @Query("UPDATE Favorite f SET f.folder = NULL WHERE f.folder = :folder AND f.deletedAt IS NULL")
    void moveAllToDefaultFolder(@Param("folder") FavoriteFolder folder);
}
