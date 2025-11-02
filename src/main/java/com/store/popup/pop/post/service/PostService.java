package com.store.popup.pop.post.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.pop.information.domain.Information;
import com.store.popup.pop.information.domain.InformationStatus;
import com.store.popup.pop.information.service.InformationService;
import com.store.popup.pop.post.domain.Post;
import com.store.popup.pop.post.dto.PostDetailDto;
import com.store.popup.pop.post.dto.PostListDto;
import com.store.popup.pop.post.dto.PostUpdateReqDto;
import com.store.popup.pop.post.dto.PostSaveDto;
import com.store.popup.pop.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final S3ClientFileUpload s3ClientFileUpload;
    private final PostMetricsService postMetricsService;
    private final InformationService informationService;
//    private final CommentService commentService;


    public Post create(PostSaveDto dto) throws AccessDeniedException {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        MultipartFile postImage = dto.getPostImage(); //게시글 사진
        String profileImgUrl = member.getProfileImgUrl();
        int reportCount = member.getReportCount();

        // 신고 횟수가 5 이상일 경우 예외 처리
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 포스트를 작성할 수 없습니다.");
        }
        Post post;
        if(postImage != null){
            String imageUrl = s3ClientFileUpload.upload(postImage);
            post = dto.toEntity(imageUrl, member, profileImgUrl);
            post = postRepository.save(post);
        }else {
            post = dto.toEntity(null, member, profileImgUrl);
            post = postRepository.save(post);
        }
        
        return post;
    }

    // 게시글 리스트
    @Transactional(readOnly = true)
    public List<PostListDto> postList(){
        List<Post> posts = postRepository.findByDeletedAtIsNull();
        // Post -> PostListDto로 변환
        return posts.stream().map(post -> {
            Long viewCount = postMetricsService.getPostViews(post.getId());   // Redis에서 조회수 가져오기
            Long likeCount = postMetricsService.getPostLikesCount(post.getId());   // Redis에서 좋아요 수 가져오기
            return post.listFromEntity(viewCount, likeCount);   // 조회수와 좋아요 수를 포함한 DTO로 변환
        }).collect(Collectors.toList());   // 리스트로 변환
    }

    // 내가 작성한 팝업 게시글 목록
    @Transactional(readOnly = true)
    public Page<PostListDto> myPostList(Pageable pageable){
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        Page<Post> posts = postRepository.findByMemberAndDeletedAtIsNull(member, pageable);
        return posts.map(post -> {
            Long viewCount = postMetricsService.getPostViews(post.getId());   // Redis에서 조회수 가져오기
            Long likeCount = postMetricsService.getPostLikesCount(post.getId());   // Redis에서 좋아요 수 가져오기
            return post.listFromEntity(viewCount, likeCount);   // 조회수와 좋아요 수를 포함한 DTO로 변환
        });
    }

    // 팝업 게시글 상세
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long id){

        Post post = postRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("존재하지 않는 post입니다."));


        // 조회수 증가 로직 추가
        postMetricsService.incrementPostViews(id);
        Long viewCount = postMetricsService.getPostViews(id);
        Long likeCount = postMetricsService.getPostLikesCount(id);

        PostDetailDto postDetailDto = PostDetailDto.fromEntity(post, viewCount, likeCount);
        return postDetailDto;
    }

    @Transactional
    public void updatePost(Long id, PostUpdateReqDto dto){
        Post post = postRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 Post입니다."));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        int reportCount = member.getReportCount();

        // 신고 횟수가 5 이상일 경우 예외 처리
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 포스트를 수정할 수 없습니다.");
        }

        MultipartFile image = dto.getPostImg();
        if (image != null && !image.isEmpty()){
            String imageUrl = s3ClientFileUpload.upload(image);
            post.updateImage(imageUrl);
        }
        post.update(dto);
        postRepository.save(post);
    }

    public void deletePost(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 post입니다."));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        int reportCount = member.getReportCount();
        if (reportCount >= 5) {
            throw new IllegalArgumentException("신고 횟수가 5회 이상인 회원은 포스트를 삭제할 수 없습니다.");
        }
        post.updateDeleteAt();
    }

    // Information을 Post로 변환 (단일)
    @Transactional
    public Post convertInformationToPost(Long informationId) {
        checkAdminRole();

        Information information = informationService.findByIds(List.of(informationId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 제보입니다."));

        if (information.getStatus() == InformationStatus.APPROVED) {
            throw new IllegalArgumentException("이미 승인된 제보입니다.");
        }

        // 중복 체크
        if (information.getAddress() != null && information.getStartDate() != null && information.getEndDate() != null) {
            Post duplicate = postRepository.findDuplicatePost(
                    information.getAddress().getCity(),
                    information.getAddress().getStreet(),
                    information.getAddress().getZipcode(),
                    information.getStartDate(),
                    information.getEndDate()
            ).orElse(null);

            if (duplicate != null) {
                throw new IllegalArgumentException("이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
            }
        }

        // Information을 Post로 변환
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member adminMember = findMemberByEmail(memberEmail);
        String profileImgUrl = adminMember.getProfileImgUrl();

        Post post = Post.builder()
                .member(adminMember)
                .title(information.getTitle())
                .content(information.getContent())
                .postImgUrl(information.getPostImgUrl())
                .profileImgUrl(profileImgUrl)
                .startDate(information.getStartDate())
                .endDate(information.getEndDate())
                .address(information.getAddress())
                .build();

        Post savedPost = postRepository.save(post);

        // Information 상태를 APPROVED로 변경
        information.approve();
        informationService.save(information);

        return savedPost;
    }

    // Information을 Post로 변환 (일괄)
    @Transactional
    public List<Post> convertInformationsToPosts(List<Long> informationIds) {
        checkAdminRole();

        List<Information> informations = informationService.findByIds(informationIds);
        if (informations.size() != informationIds.size()) {
            throw new EntityNotFoundException("일부 제보를 찾을 수 없습니다.");
        }

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member adminMember = findMemberByEmail(memberEmail);
        String profileImgUrl = adminMember.getProfileImgUrl();

        List<Post> createdPosts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Information information : informations) {
            try {
                if (information.getStatus() == InformationStatus.APPROVED) {
                    errors.add("ID " + information.getId() + ": 이미 승인된 제보입니다.");
                    continue;
                }

                // 중복 체크
                if (information.getAddress() != null && information.getStartDate() != null && information.getEndDate() != null) {
                    Post duplicate = postRepository.findDuplicatePost(
                            information.getAddress().getCity(),
                            information.getAddress().getStreet(),
                            information.getAddress().getZipcode(),
                            information.getStartDate(),
                            information.getEndDate()
                    ).orElse(null);

                    if (duplicate != null) {
                        errors.add("ID " + information.getId() + ": 이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
                        continue;
                    }
                }

                // Information을 Post로 변환
                Post post = Post.builder()
                        .member(adminMember)
                        .title(information.getTitle())
                        .content(information.getContent())
                        .postImgUrl(information.getPostImgUrl())
                        .profileImgUrl(profileImgUrl)
                        .startDate(information.getStartDate())
                        .endDate(information.getEndDate())
                        .address(information.getAddress())
                        .build();

                Post savedPost = postRepository.save(post);
                createdPosts.add(savedPost);

                // Information 상태를 APPROVED로 변경
                information.approve();
                informationService.save(information);

            } catch (Exception e) {
                errors.add("ID " + information.getId() + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty() && createdPosts.isEmpty()) {
            throw new IllegalArgumentException("모든 제보 변환 실패: " + String.join(", ", errors));
        }

        if (!errors.isEmpty()) {
            log.warn("일부 제보 변환 실패: {}", String.join(", ", errors));
        }

        return createdPosts;
    }

    // 관리자 권한 체크
    private void checkAdminRole() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
    }

    // 멤버 객체 반환
    private Member findMemberByEmail(String email){
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}

