package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.base.ErrorResponseDto;
import com.boggle_boggle.bbegok.dto.base.ResponseDto;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.service.AccessTokenService;
import com.boggle_boggle.bbegok.service.TermsService;
import com.boggle_boggle.bbegok.service.UserRefreshTokenService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import com.boggle_boggle.bbegok.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.boggle_boggle.bbegok.exception.Code.EMPTY_ACCESS_TOKEN;
import static com.boggle_boggle.bbegok.exception.Code.INVALID_ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserRefreshTokenService userRefreshTokenService;
    private final AccessTokenService accessTokenService;

    private final static long THREE_DAYS_MSEC = 259200000;

    @GetMapping("/refresh")
    public ResponseDto refreshToken (HttpServletRequest request, HttpServletResponse response) {
        //==refresh token 찾기
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElseThrow( () -> new GeneralException(Code.REFRESH_COOKIE_NOT_FOUND)
        );

        //==리프레쉬 토큰 검증하기
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
        if (!authRefreshToken.validate()) {
            return ErrorResponseDto.of(Code.INVALID_REFRESH_TOKEN);
        }

        //==userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new GeneralException(Code.INVALID_REFRESH_TOKEN));

        //==유효한 Refresh token이며 DB에도 값이 있다면 access 재발급 로직 실행
        Date now = new Date();
        AuthToken newAccessToken = accessTokenService.createAccessToken(userRefreshToken.getUserId(),
                                                                            userRefreshToken.getUser().getRoleType(),
                                                                            now);

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        //==refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(authRefreshToken.getToken());

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        return DataResponseDto.of(newAccessToken.getToken(), "New access token generated successfully");
    }

    @PostMapping("/logout")
    public ResponseDto logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.getCookie(request, DEVICE_CODE)
                .map(Cookie::getValue).ifPresent(userRefreshTokenService::deleteByDeviceId);

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.deleteCookie(request, response, DEVICE_CODE);

        return DataResponseDto.of(null, "Logout successful.");
    }
}
