package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.service.TokenService;
import com.boggle_boggle.bbegok.service.UserRefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserRefreshTokenService userRefreshTokenService;
    private final TokenService tokenService;
    private final static long THREE_DAYS_MSEC = 259200000;

    @Value("${bbaegok.root-domain}")
    private String domain;

//    @GetMapping("/refresh")
//    public ResponseDto refreshToken (HttpServletRequest request, HttpServletResponse response) {
//        //==refresh token 찾기
//        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
//                .map(Cookie::getValue)
//                .orElseThrow( () -> new GeneralException(Code.REFRESH_COOKIE_NOT_FOUND)
//        );
//
//        //==리프레쉬 토큰 검증하기
//        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
//        if (!authRefreshToken.validate()) {
//            return ErrorResponseDto.of(Code.INVALID_REFRESH_TOKEN);
//        }
//
//        //==userId refresh token 으로 DB 확인
//        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new GeneralException(Code.INVALID_REFRESH_TOKEN));
//
//        //==유효한 Refresh token이며 DB에도 값이 있다면 access 재발급 로직 실행
//        Date now = new Date();
//        AuthToken newAccessToken = accessTokenService.createAccessToken(,
//                                                                            userRefreshToken.getUser().getRoleType(),
//                                                                            now);
//        AuthToken token = tokenProvider.createAuthToken(
//                String.valueOf(userRefreshToken.getUser().getUserSeq()),
//                user.getRoleType().getCode(),
//                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
//        );
//
//        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();
//
//        //==refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
//        if (validTime <= THREE_DAYS_MSEC) {
//            // refresh 토큰 설정
//            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//
//            authRefreshToken = tokenProvider.createAuthToken(
//                    appProperties.getAuth().getTokenSecret(),
//                    new Date(now.getTime() + refreshTokenExpiry)
//            );
//
//            // DB에 refresh 토큰 업데이트
//            userRefreshToken.setRefreshToken(authRefreshToken.getToken());
//
//            int cookieMaxAge = (int) refreshTokenExpiry / 60;
//            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN, domain);
//            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge, domain);
//        }
//
//        return DataResponseDto.of(newAccessToken.getToken(), "New access token generated successfully");
//    }
//
//    @PostMapping("/logout")
//    public ResponseDto logout(HttpServletRequest request, HttpServletResponse response) {
//        CookieUtil.getCookie(request, DEVICE_CODE)
//                .map(Cookie::getValue).ifPresent(userRefreshTokenService::deleteByDeviceId);
//
//        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN, domain);
//        CookieUtil.deleteCookie(request, response, DEVICE_CODE, domain);
//
//        return DataResponseDto.of(null, "Logout successful.");
//    }
}
