package com.store.popup.favoritefolder.repository;

import com.store.popup.favoritefolder.domain.FavoriteFolder;
import com.store.popup.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
