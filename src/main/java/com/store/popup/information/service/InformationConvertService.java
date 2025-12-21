package com.store.popup.information.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostUpdateReqDto;
import com.store.popup.pop.policy.PostDuplicateValidator;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InformationConvertService {

    private final InformationService informationService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final S3ClientFileUpload s3ClientFileUpload;
    private final PostDuplicateValidator postDuplicateValidator;

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
//            ensureNoDuplicateByPlaceAndPeriod(information);
            postDuplicateValidator.ensureInfoNoDuplicateByPlaceAndPeriod(information);
        }

        // Information을 Post로 변환
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member adminMember = findMemberByEmail(memberEmail);
        String profileImgUrl = adminMember.getProfileImgUrl();

        Post post = Post.convertFromInformation(information, adminMember, profileImgUrl);
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
//                    ensureNoDuplicateByPlaceAndPeriod(information);
                    postDuplicateValidator.ensureInfoNoDuplicateByPlaceAndPeriod(information);
                }

                // Information을 Post로 변환
                Post post = Post.convertFromInformation(information, adminMember, profileImgUrl);
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

    // Information을 Post로 변환 (정보 수정 후 변환 - 단일)
    @Transactional
    public Post convertInformationToPostWithUpdate(Long informationId, PostUpdateReqDto dto) {
        checkAdminRole();

        Information information = informationService.findByIds(List.of(informationId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 제보입니다."));

        if (information.getStatus() == InformationStatus.APPROVED) {
            throw new IllegalArgumentException("이미 승인된 제보입니다.");
        }

        // 중복 체크 (수정된 정보 기준)
//        String checkCity = dto.getCity() != null ? dto.getCity() : (information.getAddress() != null ? information.getAddress().getCity() : null);
//        String checkStreet = dto.getStreet() != null ? dto.getStreet() : (information.getAddress() != null ? information.getAddress().getStreet() : null);
//        String checkZipcode = dto.getZipcode() != null ? dto.getZipcode() : (information.getAddress() != null ? information.getAddress().getZipcode() : null);
//        LocalDateTime checkStartDate = dto.getStartDate() != null ? dto.getStartDate() : information.getStartDate();
//        LocalDateTime checkEndDate = dto.getEndDate() != null ? dto.getEndDate() : information.getEndDate();
//
//        if (checkCity != null && checkStreet != null && checkZipcode != null && checkStartDate != null && checkEndDate != null) {
//            ensureNoDuplicateByPlaceAndPeriod(information);
//            postDuplicateValidator.ensureInfoNoDuplicateByPlaceAndPeriod(information);
//        }

        // Information을 Post로 변환
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member adminMember = findMemberByEmail(memberEmail);
        String profileImgUrl = adminMember.getProfileImgUrl();

        Post post = Post.convertFromInformation(information, adminMember, profileImgUrl);

        // 이미지 업로드 처리
        if (dto.getPostImg() != null && !dto.getPostImg().isEmpty()) {
            String imageUrl = s3ClientFileUpload.upload(dto.getPostImg());
            post.updateImage(imageUrl);
        }

        // 정보 수정 적용
        post.update(dto);
        // 중복 체크 (수정된 정보 기준)
        postDuplicateValidator.ensurePostNoDuplicateByPlaceAndPeriod(post);
        Post savedPost = postRepository.save(post);

        // Information 상태를 APPROVED로 변경
        information.approve();
        informationService.save(information);

        return savedPost;
    }

    // Information을 Post로 변환 (정보 수정 후 변환 - 일괄)
    @Transactional
    public List<Post> convertInformationsToPostsWithUpdate(List<Long> informationIds, List<PostUpdateReqDto> updateDtos) {
        checkAdminRole();

        if (informationIds.size() != updateDtos.size()) {
            throw new IllegalArgumentException("제보 ID 개수와 수정 정보 개수가 일치하지 않습니다.");
        }

        List<Information> informations = informationService.findByIds(informationIds);
        if (informations.size() != informationIds.size()) {
            throw new EntityNotFoundException("일부 제보를 찾을 수 없습니다.");
        }

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member adminMember = findMemberByEmail(memberEmail);
        String profileImgUrl = adminMember.getProfileImgUrl();

        List<Post> createdPosts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < informations.size(); i++) {
            Information information = informations.get(i);
            PostUpdateReqDto dto = updateDtos.get(i);

            try {
                if (information.getStatus() == InformationStatus.APPROVED) {
                    errors.add("ID " + information.getId() + ": 이미 승인된 제보입니다.");
                    continue;
                }

                // 중복 체크 (수정된 정보 기준)
//                String checkCity = dto.getCity() != null ? dto.getCity() : (information.getAddress() != null ? information.getAddress().getCity() : null);
//                String checkStreet = dto.getStreet() != null ? dto.getStreet() : (information.getAddress() != null ? information.getAddress().getStreet() : null);
//                String checkZipcode = dto.getZipcode() != null ? dto.getZipcode() : (information.getAddress() != null ? information.getAddress().getZipcode() : null);
//                LocalDateTime checkStartDate = dto.getStartDate() != null ? dto.getStartDate() : information.getStartDate();
//                LocalDateTime checkEndDate = dto.getEndDate() != null ? dto.getEndDate() : information.getEndDate();
//
//                if (checkCity != null && checkStreet != null && checkZipcode != null && checkStartDate != null && checkEndDate != null) {
////                    ensureNoDuplicateByPlaceAndPeriod(information);
//                    postDuplicateValidator.ensureInfoNoDuplicateByPlaceAndPeriod(information);
//                }

                // Information을 Post로 변환
                Post post = Post.convertFromInformation(information, adminMember, profileImgUrl);

                // 이미지 업로드 처리
                if (dto.getPostImg() != null && !dto.getPostImg().isEmpty()) {
                    String imageUrl = s3ClientFileUpload.upload(dto.getPostImg());
                    post.updateImage(imageUrl);
                }

                // 정보 수정 적용
                post.update(dto);
                // 중복 체크 (수정된 정보 기준)
                postDuplicateValidator.ensurePostNoDuplicateByPlaceAndPeriod(post);
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

    // 승인 취소: Information을 PENDING으로 되돌리고 연결된 Post를 soft delete
    @Transactional
    public InformationDetailDto cancelApproval(Long informationId) {
        checkAdminRole();

        List<Information> informations = informationService.findByIds(List.of(informationId));
        if (informations.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 제보입니다.");
        }
        Information information = informations.get(0);

        // 승인된 제보만 취소 가능
        if (information.getStatus() != InformationStatus.APPROVED) {
            throw new IllegalArgumentException("승인된 제보만 취소할 수 있습니다.");
        }

        // Information의 deletedAt을 null로 설정 (soft delete 취소)
        information.acceptHospitalAdmin();

        // Information 상태를 PENDING으로 변경
        information.resetToPending();
        // @Transactional이 있어서 더티 체킹으로 자동 저장됨

        // 연결된 Post 찾기 (같은 주소와 기간)
        if (information.getAddress() != null && information.getStartDate() != null && information.getEndDate() != null) {
            Optional<Post> relatedPost = postRepository.findDuplicatePost(
                    information.getAddress().getCity(),
                    information.getAddress().getStreet(),
                    information.getAddress().getZipcode(),
                    information.getStartDate(),
                    information.getEndDate()
            );

            // 연결된 Post를 soft delete (더티 체킹으로 자동 저장됨)
            relatedPost.ifPresent(Post::updateDeleteAt);
        }

        return InformationDetailDto.fromEntity(information);
    }

    /**
     * 사용하지 않음
     * 중복 검증 로직(일단 지우지 말기)
     * @param info
     */
    private void ensureNoDuplicateByPlaceAndPeriod(Information info) {
        Post duplicate = postRepository.findDuplicatePost(
                info.getAddress().getCity(),
                info.getAddress().getStreet(),
                info.getAddress().getZipcode(),
                info.getStartDate(),
                info.getEndDate()
        ).orElse(null);

        if (duplicate != null) {
            throw new IllegalArgumentException("이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
        }
    }
}
