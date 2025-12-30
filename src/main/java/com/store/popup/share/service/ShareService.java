package com.store.popup.share.service;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.share.dto.ShareDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ShareService {

    private final PostRepository postRepository;

    @Value("${app.base-url:https://popup.example.com}")
    private String baseUrl;

    /**
     * 공유 정보 생성
     */
    @Transactional(readOnly = true)
    public ShareDto getShareInfo(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        if (post.getDeletedAt() != null) {
            throw new EntityNotFoundException("삭제된 게시글입니다.");
        }

        String shareUrl = baseUrl + "/post/" + postId;

        return ShareDto.builder()
                .postId(post.getId())
                .shareUrl(shareUrl)
                .title(post.getTitle())
                .description(post.getContent().length() > 100 ?
                        post.getContent().substring(0, 100) + "..." : post.getContent())
                .imageUrl(post.getPostImgUrl())
                .shareCount(post.getShareCount())
                .build();
    }

    /**
     * 공유 횟수 증가
     */
    public void incrementShareCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // dirty checking
        post.incrementShareCount();
        log.info("Post {} 공유 횟수 증가: {}", postId, post.getShareCount());
    }
}
