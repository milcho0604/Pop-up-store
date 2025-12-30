package com.store.popup.favoritefolder.service;

import com.store.popup.favorite.domain.Favorite;
import com.store.popup.favorite.repository.FavoriteRepository;
import com.store.popup.favoritefolder.domain.FavoriteFolder;
import com.store.popup.favoritefolder.dto.FavoriteFolderDto;
import com.store.popup.favoritefolder.dto.FavoriteFolderSaveDto;
import com.store.popup.favoritefolder.repository.FavoriteFolderRepository;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    /**
     * 폴더 생성
     */
    public FavoriteFolderDto createFolder(FavoriteFolderSaveDto dto) {
        Member member = getCurrentMember();

        // 폴더 이름 중복 체크
        if (favoriteFolderRepository.existsByMemberAndNameAndDeletedAtIsNull(member, dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 폴더 이름입니다.");
        }

        FavoriteFolder folder = FavoriteFolder.builder()
                .member(member)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        FavoriteFolder savedFolder = favoriteFolderRepository.save(folder);
        return FavoriteFolderDto.fromEntity(savedFolder);
    }

    /**
     * 내 폴더 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FavoriteFolderDto> getMyFolders() {
        Member member = getCurrentMember();
        List<FavoriteFolder> folders = favoriteFolderRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtAsc(member);
        return folders.stream()
                .map(FavoriteFolderDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 폴더 수정
     */
    public FavoriteFolderDto updateFolder(Long folderId, FavoriteFolderSaveDto dto) {
        Member member = getCurrentMember();
        FavoriteFolder folder = favoriteFolderRepository.findByIdAndMemberAndDeletedAtIsNull(folderId, member)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 폴더입니다."));

        // 다른 폴더와 이름 중복 체크
        if (!folder.getName().equals(dto.getName()) &&
                favoriteFolderRepository.existsByMemberAndNameAndDeletedAtIsNull(member, dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 폴더 이름입니다.");
        }

        // dirty checking
        folder.updateName(dto.getName());
        folder.updateDescription(dto.getDescription());

        return FavoriteFolderDto.fromEntity(folder);
    }

    /**
     * 폴더 삭제
     */
    public void deleteFolder(Long folderId) {
        Member member = getCurrentMember();
        FavoriteFolder folder = favoriteFolderRepository.findByIdAndMemberAndDeletedAtIsNull(folderId, member)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 폴더입니다."));

        // 폴더 내 찜을 기본 폴더로 이동
        List<Favorite> favoritesInFolder = favoriteRepository.findByFolderAndDeletedAtIsNull(folder);
        favoritesInFolder.forEach(fav -> fav.moveToFolder(null));

        // soft delete
        folder.updateDeleteAt();
    }

    /**
     * 찜을 폴더로 이동
     */
    public void moveFavoriteToFolder(Long favoriteId, Long folderId) {
        Member member = getCurrentMember();
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 찜입니다."));

        // 본인의 찜인지 확인
        if (!favorite.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 찜만 이동할 수 있습니다.");
        }

        FavoriteFolder folder = null;
        if (folderId != null) {
            folder = favoriteFolderRepository.findByIdAndMemberAndDeletedAtIsNull(folderId, member)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 폴더입니다."));
        }

        // dirty checking
        favorite.moveToFolder(folder);
        log.info("찜 {} 를 폴더 {} 로 이동", favoriteId, folderId);
    }

    // ========== Helper Methods ==========

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
