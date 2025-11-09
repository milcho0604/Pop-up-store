package com.store.popup.pop.policy;


import com.store.popup.information.domain.Information;
import com.store.popup.member.domain.Address;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostDuplicateValidator {

    private final PostRepository postRepository;

    /** Information 기반 중복 체크 (null-safe) */
    @Transactional(readOnly = true)
    public void ensureInfoNoDuplicateByPlaceAndPeriod(Information info) {
        if (info == null || info.getAddress() == null
                || info.getStartDate() == null || info.getEndDate() == null) {
            return; // 주소/기간이 없으면 정책상 체크 스킵
        }
        Address addr = info.getAddress();
        boolean duplicated = postRepository
                .existsByAddress_CityAndAddress_StreetAndAddress_ZipcodeAndStartDateAndEndDateAndDeletedAtIsNull(
                        addr.getCity(), addr.getStreet(), addr.getZipcode(),
                        info.getStartDate(), info.getEndDate()
                );
        if (duplicated) {
            throw new IllegalArgumentException("이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
        }
    }

    /** Post 엔티티(혹은 값) 기반 중복 체크 (생성/변환용) */
    @Transactional(readOnly = true)
    public void ensurePostNoDuplicateByPlaceAndPeriod(Post post) {
        if (post == null || post.getAddress() == null
                || post.getStartDate() == null || post.getEndDate() == null) {
            return;
        }
        Address addr = post.getAddress();
        boolean duplicated = postRepository
                .existsByAddress_CityAndAddress_StreetAndAddress_ZipcodeAndStartDateAndEndDateAndDeletedAtIsNull(
                        addr.getCity(), addr.getStreet(), addr.getZipcode(),
                        post.getStartDate(), post.getEndDate()
                );
        if (duplicated) {
            throw new IllegalArgumentException("이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
        }
    }

    /** 업데이트용: 자기 자신(excludePostId)을 제외하고 중복 체크 */
    @Transactional(readOnly = true)
    public void ensureNoDuplicateForUpdate(Long excludePostId,
                                           Address address,
                                           LocalDateTime start,
                                           LocalDateTime end) {
        if (address == null || start == null || end == null) return;

        boolean duplicated = postRepository
                .existsByIdNotAndAddress_CityAndAddress_StreetAndAddress_ZipcodeAndStartDateAndEndDateAndDeletedAtIsNull(
                        excludePostId,
                        address.getCity(), address.getStreet(), address.getZipcode(),
                        start, end
                );
        if (duplicated) {
            throw new IllegalArgumentException("이미 등록된 팝업 스토어입니다. (중복된 주소와 운영 기간)");
        }
    }
}