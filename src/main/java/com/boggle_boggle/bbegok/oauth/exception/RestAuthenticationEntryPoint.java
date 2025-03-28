package com.boggle_boggle.bbegok.oauth.exception;

import com.boggle_boggle.bbegok.dto.base.ErrorResponseDto;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/** 인증되지 않은 요청이 보호된 리소스에 접근할때 호출
 * 즉, 액세스토큰이 유효하지 않을때 동작한다.
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                            HttpServletResponse response,
                            AuthenticationException authException) throws IOException {
        ErrorResponseDto errorResponse = ErrorResponseDto.of(Code.INVALID_ACCESS_TOKEN);
        response.setStatus(Code.INVALID_ACCESS_TOKEN.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
