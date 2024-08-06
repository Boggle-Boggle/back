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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserRefreshTokenService clearRefreshToken;

    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @GetMapping("/refresh")
    public ResponseDto refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            return ErrorResponseDto.of(Code.JWT_INVALID_TOKEN, "Invalid access token");
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return ErrorResponseDto.of(Code.TOKEN_NOT_EXPIRED, "Access token is not expired yet");
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        if (refreshToken == null) {
            return ErrorResponseDto.of(Code.REFRESH_TOKEN_NOT_FOUND, "Refresh token not found in cookie");
        }

        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
        if (!authRefreshToken.validate()) {
            return ErrorResponseDto.of(Code.INVALID_REFRESH_TOKEN, "Invalid refresh token. Please Sign-in");
        }
        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
        if (userRefreshToken == null) {
            return ErrorResponseDto.of(Code.INVALID_REFRESH_TOKEN, "Refresh token not found for user. Please Sign-in");
        }

        //유효한 Refresh token이며 DB에도 값이 있다면 access 재발급 로직 실행
        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
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
        System.out.println("logout 진입");
        // 1. 헤더에서 액세스 토큰 가져오기
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        System.out.println("액세스 토큰으로 userId 찾기");
        if (accessToken != null) {
            System.out.println("userId 찾기");
            AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
            Claims claims = null;

            System.out.println("유효한가?" +authToken.validate());
            if (authToken.validate()) claims = authToken.getTokenClaims();
            else claims = authToken.getExpiredTokenClaims();

            if (claims != null) {
                String userId = claims.getSubject();
                System.out.println("userId = "+userId);
                clearRefreshToken.deleteRefreshTokenByUserId(userId);

                // 리프레시 토큰 삭제
                clearRefreshToken.deleteRefreshTokenByUserId(userId);
            }

        }

        // 쿠키에서 리프레시 토큰 제거
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);

        return DataResponseDto.of(null, "Logout successful.");
    }
}
