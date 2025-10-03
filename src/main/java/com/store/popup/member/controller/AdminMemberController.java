package com.store.popup.member.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.member.dto.MemberListResDto;
import com.store.popup.member.service.AdminMemberServcie;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/member")
public class AdminMemberController {

    private final AdminMemberServcie adminMemberServcie;
//    @PreAuthorize("hasRole('ADMIN')")
    /**
     * 관리자 회원 리스트 조회
     *
     * 예시 요청:
     * /admin/member/list?isVerified=true&isDeleted=false&role=USER&page=0&size=20
     */
    @GetMapping("/list")
    public ResponseEntity<?> memberList(
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String role,
            Pageable pageable) {

        Page<MemberListResDto> memberListResDtos = adminMemberServcie.memberList(isVerified, isDeleted, role, pageable);

        CommonResDto dto = new CommonResDto(HttpStatus.OK, "회원 목록을 조회합니다.", memberListResDtos);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
