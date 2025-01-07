package com.boggle_boggle.bbegok.oauth.handler;

import com.boggle_boggle.bbegok.dto.base.ErrorResponseDto;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.utils.HeaderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final AuthTokenProvider tokenProvider;

    /** 권한이 없는 자원에 접근할때 실행됨(시큐리티에서 자동으로 사용함)
     * @param request
     * @param response
     * @param accessDeniedException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        tokenProvider.validateRole(token);

        throw new GeneralException(Code.GUEST_DENIED_ACCESS);
    }
}