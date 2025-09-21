package com.store.popup.member.service;


import com.store.popup.common.config.JwtTokenProvider;
import com.store.popup.common.util.S3ClientFileUpload;
import com.store.popup.member.domain.Member;
import com.store.popup.member.dto.MemberLoginDto;
import com.store.popup.member.dto.MemberSaveReqDto;
import com.store.popup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final S3ClientFileUpload s3ClientFileUpload;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 및 검증
    public void create(MemberSaveReqDto saveReqDto, MultipartFile imageSsr) {
        validateRegistration(saveReqDto);

        // 프로필 이미지 업로드 및 url로 저장 -> aws에서 이미지를 가져오는 방식
        String imageUrl = null;
        if (saveReqDto.getProfileImage() != null && !saveReqDto.getProfileImage().isEmpty()) {
            imageUrl = s3ClientFileUpload.upload(saveReqDto.getProfileImage());
            saveReqDto.setProfileImgUrl(imageUrl);
        }


        Member member = saveReqDto.toEntity(passwordEncoder.encode(saveReqDto.getPassword()));
        memberRepository.save(member);
    }
    private void validateRegistration(MemberSaveReqDto saveReqDto) {
        if (saveReqDto.getPassword().length() <= 7) {
            throw new RuntimeException("비밀번호는 8자 이상이어야 합니다.");
        }

        if (memberRepository.existsByMemberEmail(saveReqDto.getMemberEmail())) {
            throw new RuntimeException("이미 사용중인 이메일 입니다.");
        }
    }

    // 로그인
    public String login(MemberLoginDto loginDto) {
        Member member = memberRepository.findByMemberEmail(loginDto.getMemberEmail())
                .orElseThrow(() -> new RuntimeException("잘못된 이메일/비밀번호 입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("잘못된 이메일/비밀번호 입니다.");
        }

        if (member.getDeletedAt() != null){
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        memberRepository.save(member);

        return jwtTokenProvider.createToken(member.getMemberEmail(), member.getRole().name(), member.getId());
    }


}
