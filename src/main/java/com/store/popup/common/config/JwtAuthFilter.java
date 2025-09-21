package com.store.popup.common.config;



import com.store.popup.member.domain.Member;
import com.store.popup.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.memberRepository = memberRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (userDetails != null) {
                Member member = memberRepository.findByMemberEmail(email)
                        .orElse(null);

                if (member == null || member.getMemberEmail() == null || member.getDeletedAt() != null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("해당 계정은 영구 정지 되었습니다.");
                    return;
                }

                // 권한 확인 로그 추가
//                for (GrantedAuthority authority : userDetails.getAuthorities()) {
//                    System.out.println("권한: " + authority.getAuthority());
//                }

                // 권한과 함께 SecurityContext 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, token, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
