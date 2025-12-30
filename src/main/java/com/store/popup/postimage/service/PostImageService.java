package com.store.popup.postimage.service;

import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.postimage.domain.PostImage;
import com.store.popup.postimage.dto.PostImageDto;
import com.store.popup.postimage.repository.PostImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;

    /**
     * Post에 이미지 추가 (여러 장 가능)
     */
    public List<PostImageDto> addImages(Long postId, List<MultipartFile> files, List<String> descriptions) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 작성자 확인
        if (!post.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("게시글 작성자만 이미지를 추가할 수 있습니다.");
        }

        // 현재 이미지 개수 확인 (순서 지정용)
        Long currentCount = postImageRepository.countByPostAndDeletedAtIsNull(post);
        int displayOrder = currentCount.intValue();

        List<PostImage> savedImages = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String description = (descriptions != null && i < descriptions.size()) ? descriptions.get(i) : null;

            // S3에 이미지 업로드
            String imageUrl = s3ClientFileUpload.upload(file);

            // PostImage 엔티티 생성
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .displayOrder(displayOrder++)
                    .description(description)
                    .build();

            savedImages.add(postImageRepository.save(postImage));
        }

        return savedImages.stream()
                .map(PostImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Post의 모든 이미지 조회
     */
    @Transactional(readOnly = true)
    public List<PostImageDto> getPostImages(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        List<PostImage> images = postImageRepository.findByPostAndDeletedAtIsNullOrderByDisplayOrderAsc(post);
        return images.stream()
                .map(PostImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 삭제
     */
    public void deleteImage(Long imageId) {
        Member member = getCurrentMember();
        PostImage postImage = postImageRepository.findByIdWithPostAndMember(imageId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이미지입니다."));

        // 작성자 확인
        if (!postImage.getPost().getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("게시글 작성자만 이미지를 삭제할 수 있습니다.");
        }

        // soft delete
        postImage.updateDeleteAt();
    }

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getCurrentMember() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
