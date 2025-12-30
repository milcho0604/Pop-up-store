package com.store.popup.favoritefolder.repository;

import com.store.popup.favoritefolder.domain.FavoriteFolder;
import com.store.popup.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    // 회원의 모든 폴더 조회
    List<FavoriteFolder> findByMemberAndDeletedAtIsNullOrderByCreatedAtAsc(Member member);

    // 폴더 이름 중복 체크
    boolean existsByMemberAndNameAndDeletedAtIsNull(Member member, String name);

    // 특정 폴더 조회
    Optional<FavoriteFolder> findByIdAndMemberAndDeletedAtIsNull(Long id, Member member);

    // 폴더별 찜 개수 조회 (N+1 방지)
    @Query("SELECT f.id, COUNT(fav.id) FROM FavoriteFolder f LEFT JOIN f.favorites fav " +
           "WHERE f.member = :member AND f.deletedAt IS NULL AND (fav.deletedAt IS NULL OR fav IS NULL) " +
           "GROUP BY f.id")
    List<Object[]> countFavoritesByMember(@Param("member") Member member);
}
