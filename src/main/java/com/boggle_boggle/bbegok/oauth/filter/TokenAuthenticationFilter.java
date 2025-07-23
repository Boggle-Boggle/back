package com.boggle_boggle.bbegok.oauth.filter;

import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.utils.HeaderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {

        // 인증 제외 경로는 그냥 패스
        String uri = request.getRequestURI();
        if (uri.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenStr = HeaderUtil.getAccessToken(request);
        // 토큰 없으면 인증 시도하지 않음
        if (tokenStr == null || tokenStr.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        
        if (token.validate()) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication); //인증객체 셋팅
        }

        filterChain.doFilter(request, response);
    }
}