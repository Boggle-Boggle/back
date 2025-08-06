package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.config.properties.CorsProperties;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.TokenDto;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.SignupRequest;
import com.boggle_boggle.bbegok.dto.response.AccessTokenResponse;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import com.boggle_boggle.bbegok.enums.SignStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.client.OAuth2RedirectUriBuilder;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.service.OAuth2LoginService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import com.boggle_boggle.bbegok.utils.OauthValidateUtil;
import jakarta.persistence.Access;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuth2AuthController {
    private static final String preSignupIdCookieName = "pre_signup_id";
    private final OAuth2LoginService oauth2LoginService;
    private final QueryService queryService;
    private final UserService userService;
    private final OAuth2RedirectUriBuilder oAuth2RedirectUriBuilder;
    private final CorsProperties corsProperties;

    //refresh token을 바탕으로 accessToken응답
    @GetMapping("/refresh")
    public DataResponseDto<AccessTokenResponse> refresh(HttpServletRequest request,
                                                        HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue).orElseThrow( () -> new GeneralException(Code.REFRESH_COOKIE_NOT_FOUND));
        TokenDto dto = oauth2LoginService.refresh(refreshToken);

        if(dto.isRefreshUpdated()) queryService.updateRefreshCookie(request, response, dto.getRefreshToken());

        AccessTokenResponse resp = AccessTokenResponse.builder().accessToken(dto.getAccessToken()).build();
        return DataResponseDto.of(resp);
    }


    //회원가입
    @PostMapping("/signup")
    public DataResponseDto<Void> signup(@Valid @RequestBody SignupRequest signupRequest,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        Long preSignupId = CookieUtil.getCookie(request, preSignupIdCookieName)
                .map(Cookie::getValue)
                .map(Long::parseLong)
                .orElseThrow(() -> new GeneralException(Code.SIGNUP_NOTFOUND));

        OAuthLoginResponse oauthLoginResponse = userService.signup(preSignupId, signupRequest.getNickname(), signupRequest.getAgreements());
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }

        return DataResponseDto.empty();
    }

    //각 인증서버로 리다이렉트
    @GetMapping("/oauth2/authorize")
    public void authorize(@RequestParam("provider") ProviderType providerType,
                          @RequestParam("redirect") String redirectFront, HttpSession session,
                                                          HttpServletResponse response) throws IOException {
        List<String> origins = Arrays.stream(corsProperties.getAllowedOrigins().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        if (origins.stream().noneMatch(redirectFront::startsWith)) {
            response.sendError(400, "invalid front url");
            return;
        }

        //리다이렉트 URI 및 csrf방지 state를 세션에 저장
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth2_state", state);
        session.setAttribute("redirect_front", redirectFront);

        String redirectUrl = oAuth2RedirectUriBuilder.buildRedirectUri(providerType, state);
        response.sendRedirect(redirectUrl); // 실제 리디렉션
    }

    //oauth 인증서버의 콜백 API
    @GetMapping("/oauth2/callback/{provider}")
    public void oauth2Callback(
            @PathVariable("provider") ProviderType providerType,
            @RequestParam("code") String code,
            @RequestParam(name="state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        OauthValidateUtil.validateState(request, state);
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(providerType, code, state);

        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) { //기존유저 - RefreshToken 및 DiviceId만 쿠키에 포함해서 리다이렉트
            queryService.setLoginCookie(request, response, oauthLoginResponse);
        } else if(oauthLoginResponse.getStatus() == SignStatus.SIGNUP_REQUIRED) { //신규유저 - preSignupUd를 쿠키에 포함해서 리다이렉트
            queryService.setPreSignupCookie(request, response, oauthLoginResponse.getPreSignupId());
        } else {
            throw new GeneralException(Code.BAD_REQUEST);
        }

        //https://{프론트}/auth?status={}'으로 redirect
        HttpSession session = request.getSession();
        String redirectFront = (String) session.getAttribute("redirect_front");
        if (redirectFront == null || corsProperties.getAllowedOrigins().lines().noneMatch(redirectFront::startsWith)) {
            response.sendError(400, "invalid redirect front url");
            return;
        }

        session.removeAttribute("redirect_front");
        session.removeAttribute("oauth2_state");

        String frontUrl = UriComponentsBuilder
                .fromUriString(redirectFront)
                .path("/auth")
                .queryParam("status", oauthLoginResponse.getStatus())
                .build()
                .toUriString();

        response.sendRedirect(frontUrl);
    }

    //APPLE(POST) 전용 콜백 API
    @PostMapping("/oauth2/callback/apple")
    public DataResponseDto<OAuthLoginResponse> oauth2AppkeCallback(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {
        OauthValidateUtil.validateState(request, state);
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(ProviderType.APPLE, code, state);
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }

        //'auth'페이지로 리디렉션
        return DataResponseDto.of(oauthLoginResponse);
    }

    //accessToken 응답
}
