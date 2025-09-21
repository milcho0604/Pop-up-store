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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/member")
public class AdminMemberController {

    private final AdminMemberServcie adminMemberServcie;
    // 멤버 리스트 조회
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> memberList(
            Pageable pageable) {

        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_ANONYMOUS")
                .replace("ROLE_", "");

        System.out.println("최종 role: " + role);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<MemberListResDto> memberListResDtos = adminMemberServcie.memberList(email, role, pageable);

        CommonResDto dto = new CommonResDto(HttpStatus.OK, "회원목록을 조회합니다.", memberListResDtos);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
