package com.store.popup.member.Handler;//package com.event.myevent.member.Handler;
//
//import com.event.myevent.common.config.JwtTokenProvider;
//import com.event.myevent.member.repository.MemberRepository;
//import jakarta.servlet.http.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//
//
//import java.io.IOException;
//import java.util.Map;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtTokenProvider jwtTokenprovider;
//    private final MemberRepository memberRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
//            , Authentication authentication) throws IOException {
//
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String memberEmail = oAuth2User.getAttribute("memberEmail");
//
//        if (memberEmail == null) {
//            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
//            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
//                memberEmail = (String) kakaoAccount.get("email");
//            }
//            if (memberEmail == null) {
//                Object naverObject = oAuth2User.getAttributes().get("response");
//                Map<String, Object> naverAccount = (Map<String, Object>) naverObject;
//                if (naverAccount != null && naverAccount.containsKey("email")) {
//                    memberEmail = (String) naverAccount.get("email");
//                }
//            }
//        }
//
//
//        Member member = memberRepository.findByMemberEmail(memberEmail)
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카카오 회원입니다."));
//
//        if (member.getDeletedAt() != null){
//            throw new IllegalStateException("탈퇴한 회원입니다.");
//        }
//
//        Long getMemberId = member.getId();
//
//        String token = jwtTokenprovider.kakaoToken(memberEmail, getMemberId,member.getRole().toString());
//
//        // 리다이렉트 URL 설정 (아래 먼저는 로컬호스트 환경에서, 두번째는 프론트 환경에서)
//        String targetUrl = null;
//
//        // local 환경에서 targetUrl
////        if (member.isVerified() == true){
////            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8081/all/loginSuccess")
////                    .queryParam("token", token)
////                    .build().toUriString();
////        } else {
////            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8081/all/updateSuccess")
////                    .queryParam("token", token)
////                    .build().toUriString();
////        }
//
//        // 배포 환경에서 targetUrl
//        if (member.isVerified() == true){
//            targetUrl = UriComponentsBuilder.fromUriString("https://www.todak.site/all/loginSuccess")
//                    .queryParam("token", token)
//                    .build().toUriString();
//        } else {
//            targetUrl = UriComponentsBuilder.fromUriString("https://www.todak.site/all/updateSuccess")
//                    .queryParam("token", token)
//                    .build().toUriString();
//        }
//        // 세션에 사용자 정보 저장
////        HttpSession session = request.getSession();
////        session.setAttribute("user", memberEmail); // 사용자 정보를 세션에 저장
//
//        // 리다이렉트 수행
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//}