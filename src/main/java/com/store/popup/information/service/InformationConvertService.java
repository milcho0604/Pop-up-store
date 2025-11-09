package com.store.popup.information.service;

import com.store.popup.common.enumdir.Role;
import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InformationConvertService {

    private final InformationService informationService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

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
                .phoneNumber(information.getPhoneNumber())
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
                        .phoneNumber(information.getPhoneNumber())
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
