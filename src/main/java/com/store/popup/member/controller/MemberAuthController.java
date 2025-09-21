package com.store.popup.member.controller;


import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.member.dto.MemberLoginDto;
import com.store.popup.member.dto.MemberSaveReqDto;
import com.store.popup.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 회원가입
    @PostMapping("/sign")
    public ResponseEntity<?> register(MemberSaveReqDto saveReqDto,
                                      @RequestPart(value = "image", required = false) MultipartFile imageSsr) {
        try {
            memberService.create(saveReqDto, imageSsr);
            String info = "아이디: " + saveReqDto.getMemberEmail();
            return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "회원가입 성공", info));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResDto(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다: " + e.getMessage(), null));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDto loginDto) {
        try {
            String token = memberService.login(loginDto);
            return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "로그인 성공", token));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CommonErrorDto(HttpStatus.FORBIDDEN, "관계자만 로그인이 가능합니다. 필요합니다."));
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().contains("비활성화 상태")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new CommonErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new CommonErrorDto(HttpStatus.UNPROCESSABLE_ENTITY, "잘못된 이메일/비밀번호입니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 중 오류가 발생했습니다."));
        }
    }
}
