package com.store.popup.pop.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.information.service.InformationService;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostDetailDto;
import com.store.popup.pop.dto.PostListDto;
import com.store.popup.pop.dto.PostUpdateReqDto;
import com.store.popup.pop.dto.PostSaveDto;
import com.store.popup.pop.dto.SearchFilterReqDto;
import com.store.popup.pop.policy.PostDuplicateValidator;
import com.store.popup.pop.repository.PostRepository;
import com.store.popup.pop.repository.PostSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
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
    private final PostDuplicateValidator postDuplicateValidator;
//    private final CommentService commentService;


    // 팝업 게시글 작성 -> 테스트를 위해 관리자는 중복 검증 로직 회피
    public Post create(PostSaveDto dto) throws AccessDeniedException {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        MultipartFile postImage = dto.getPostImage(); //게시글 사진
        String profileImgUrl = member.getProfileImgUrl();

        Post post;
        if(postImage != null){
            String imageUrl = s3ClientFileUpload.upload(postImage);
            post = dto.toEntity(imageUrl, member, profileImgUrl);
//            postDuplicateValidator.ensurePostNoDuplicateByPlaceAndPeriod(post);
            post = postRepository.save(post);
        }else {
            post = dto.toEntity(null, member, profileImgUrl);
//            postDuplicateValidator.ensurePostNoDuplicateByPlaceAndPeriod(post);
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

    // 팝업 게시글 수정
    @Transactional
    public void updatePost(Long id, PostUpdateReqDto dto){
        Post post = postRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 Post입니다."));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = findMemberByEmail(memberEmail);
        int reportCount = member.getReportCount();

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

    // 검색 및 필터링 (정렬 포함)
    @Transactional(readOnly = true)
    public Page<PostListDto> searchAndFilter(SearchFilterReqDto searchFilter, Pageable pageable) {
        // Specification 생성
        Specification<Post> spec = PostSpecification.searchWithFilters(searchFilter);

        // 정렬 기준 설정
        Sort sort = getSort(searchFilter.getSortBy());
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 검색 실행
        Page<Post> posts = postRepository.findAll(spec, pageableWithSort);

        // Post -> PostListDto 변환
        return posts.map(post -> {
            Long viewCount = postMetricsService.getPostViews(post.getId());
            Long likeCount = postMetricsService.getPostLikesCount(post.getId());
            return post.listFromEntity(viewCount, likeCount);
        });
    }

    // 검색 및 필터링 (List 반환, 페이징 없음)
    @Transactional(readOnly = true)
    public List<PostListDto> searchAndFilterList(SearchFilterReqDto searchFilter) {
        // Specification 생성
        Specification<Post> spec = PostSpecification.searchWithFilters(searchFilter);

        // 정렬 기준 설정
        Sort sort = getSort(searchFilter.getSortBy());

        // 검색 실행
        List<Post> posts = postRepository.findAll(spec, sort);

        // Post -> PostListDto 변환
        List<PostListDto> postListDtos = posts.stream().map(post -> {
            Long viewCount = postMetricsService.getPostViews(post.getId());
            Long likeCount = postMetricsService.getPostLikesCount(post.getId());
            return post.listFromEntity(viewCount, likeCount);
        }).collect(Collectors.toList());

        // 인기순, 조회수순, 마감임박순은 Redis 데이터 기반 정렬이 필요하므로 추가 정렬
        if (searchFilter.getSortBy() != null) {
            switch (searchFilter.getSortBy()) {
                case POPULAR:
                    postListDtos.sort(Comparator.comparingLong(PostListDto::getLikeCount).reversed());
                    break;
                case VIEW_COUNT:
                    postListDtos.sort(Comparator.comparingLong(PostListDto::getViewCount).reversed());
                    break;
                case ENDING_SOON:
                    postListDtos.sort(Comparator.comparing(PostListDto::getEndDate));
                    break;
                default:
                    // LATEST는 이미 DB에서 정렬됨
                    break;
            }
        }

        return postListDtos;
    }

    // 정렬 기준에 따른 Sort 객체 생성
    private Sort getSort(SearchFilterReqDto.SortType sortType) {
        if (sortType == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt"); // 기본: 최신순
        }

        switch (sortType) {
            case LATEST:
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case ENDING_SOON:
                return Sort.by(Sort.Direction.ASC, "endDate");
            case POPULAR:
            case VIEW_COUNT:
                // 인기순과 조회수순은 Redis 데이터 기반이므로 DB 정렬은 최신순으로
                return Sort.by(Sort.Direction.DESC, "createdAt");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }

    // 멤버 객체 반환
    private Member findMemberByEmail(String email){
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}

