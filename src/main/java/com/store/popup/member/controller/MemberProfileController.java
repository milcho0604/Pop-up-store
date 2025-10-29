package com.store.popup.member.controller;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.member.dto.MemberProfileResDto;
import com.store.popup.member.dto.MemberProfileUpdateReqDto;
import com.store.popup.member.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Slf4j
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            MemberProfileResDto res = memberProfileService.getMyProfile();
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "프로필 조회 성공", res);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto error = new CommonErrorDto(HttpStatus.BAD_REQUEST, "프로필 조회 실패: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    // 파일 업로드(form-data)를 지원하기 위해 @ModelAttribute 사용
    @PostMapping("/me")
    public ResponseEntity<?> updateMyProfile(@ModelAttribute MemberProfileUpdateReqDto dto) {
        try {
            MemberProfileResDto res = memberProfileService.updateMyProfile(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "프로필 업데이트 성공", res);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto error = new CommonErrorDto(HttpStatus.BAD_REQUEST, "프로필 업데이트 실패: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
