package com.store.popup.member.controller;


import com.store.popup.common.dto.CommonResDto;
import com.store.popup.member.dto.*;
import com.store.popup.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberAuthController {

    private final MemberAuthService memberService;

    // 인증번호 전송
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody EmailVerificationDto verificationDto) {
        memberService.sendVerificationEmail(verificationDto.getEmail());
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "인증 코드 전송 성공", null));
    }
    // 이메일 인증
    @PostMapping("/verify-email")
    public ResponseEntity<CommonResDto> verifyEmail(@RequestBody EmailVerificationDto verificationDto) {
        boolean isVerified = memberService.verifyEmail(verificationDto.getEmail(), verificationDto.getCode());
        if (isVerified) {
            return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "이메일 인증 성공", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResDto(HttpStatus.BAD_REQUEST, "이메일 인증에 실패했습니다.", null));
        }
    }

    // 회원가입
    @PostMapping("/sign")
    public ResponseEntity<CommonResDto> register(MemberSaveReqDto saveReqDto,
                                      @RequestPart(value = "image", required = false) MultipartFile imageSsr) {
        memberService.create(saveReqDto, imageSsr);
        String info = "아이디: " + saveReqDto.getMemberEmail();
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "회원가입 성공", info));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResDto> login(@RequestBody MemberLoginDto loginDto) {
        String token = memberService.login(loginDto);
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "로그인 성공", token));
    }

    // 비밀번호 재설정 링크 전송
    @PostMapping("/find/password")
    public ResponseEntity<CommonResDto> findPassword(@RequestBody MemberFindPasswordDto dto) {
        memberService.sendPasswordResetLink(dto);
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "비밀번호 재설정 링크를 전송하였습니다.", dto));
    }

    // 비밀번호 재설정
    @PostMapping("/reset/password")
    public ResponseEntity<CommonResDto> resetPassword(@RequestBody PasswordResetDto dto) {
        memberService.resetPassword(dto);
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "비밀번호 재설정에 성공하였습니다.", HttpStatus.OK));
    }

    // 비밀번호 찾기를 위한 토큰 로직
    @GetMapping("/reset/password")
    public ResponseEntity<?> showResetPasswordPage(@RequestParam("token") String token) {
        // 토큰 유효성 검사 등 추가 로직 수행 가능

        // 이 단계에서 Vue.js로의 페이지 렌더링을 의도
        // ResponseEntity는 JSON 응답을 반환하는 대신 Vue.js 페이지로 리디렉션합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/reset/password?token=" + token);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


}
