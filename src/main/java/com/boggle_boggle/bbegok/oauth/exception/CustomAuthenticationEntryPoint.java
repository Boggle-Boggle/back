package com.boggle_boggle.bbegok.oauth.exception;

import com.boggle_boggle.bbegok.dto.base.ErrorResponseDto;
import com.boggle_boggle.bbegok.exception.Code;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 시큐리티 에러 핸들러
 * 인증되지 않은 요청이 보호된 리소스에 접근할때 호출(401, UNAUTHORIZED)
 * 즉, 액세스토큰이 유효하지 않을때 동작한다.
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                            HttpServletResponse response,
                            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message = authException.getMessage();  // 예외에 담긴 메시지

        ErrorResponseDto<Object> errorBody = ErrorResponseDto.of(Code.INVALID_ACCESS_TOKEN);
        String json = objectMapper.writeValueAsString(errorBody);
        response.getWriter().write(json);
    }
}
