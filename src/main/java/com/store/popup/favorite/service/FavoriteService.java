package com.store.popup.favorite.service;

import com.store.popup.favorite.domain.Favorite;
import com.store.popup.favorite.dto.FavoriteResDto;
import com.store.popup.favorite.repository.FavoriteRepository;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 찜하기 추가
    public FavoriteResDto addFavorite(Long postId) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        // 이미 찜했는지 확인
        if (favoriteRepository.existsByMemberAndPost(member, post)) {
            throw new IllegalArgumentException("이미 찜한 포스트입니다.");
        }

        // 삭제된 포스트는 찜할 수 없음
        if (post.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 포스트는 찜할 수 없습니다.");
        }

        Favorite favorite = Favorite.builder()
                .member(member)
                .post(post)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResDto.fromEntity(savedFavorite);
    }

    // 찜하기 취소
    public void removeFavorite(Long postId) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        Favorite favorite = favoriteRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new EntityNotFoundException("찜하지 않은 포스트입니다."));

        // soft delete
        favorite.updateDeleteAt();
    }

    // 내 찜 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<FavoriteResDto> getMyFavorites(Pageable pageable) {
        Member member = getCurrentMember();
        Page<Favorite> favorites = favoriteRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member, pageable);
        return favorites.map(FavoriteResDto::fromEntity);
    }

    // 내 찜 목록 조회 (전체 리스트)
    @Transactional(readOnly = true)
    public List<FavoriteResDto> getMyFavoritesList() {
        Member member = getCurrentMember();
        List<Favorite> favorites = favoriteRepository.findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);
        return favorites.stream()
                .map(FavoriteResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 찜 여부 확인
    @Transactional(readOnly = true)
    public boolean isFavorite(Long postId) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 포스트입니다."));

        return favoriteRepository.existsByMemberAndPost(member, post);
    }

    // 내 찜 개수
    @Transactional(readOnly = true)
    public long getMyFavoriteCount() {
        Member member = getCurrentMember();
        return favoriteRepository.countByMemberAndDeletedAtIsNull(member);
    }

    // Post의 찜 개수
    @Transactional(readOnly = true)
    public long getPostFavoriteCount(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("존재하지 않는 포스트입니다.");
        }
        return favoriteRepository.countByPostId(postId);
    }

    // 현재 로그인한 회원 조회
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
