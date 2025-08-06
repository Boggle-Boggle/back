package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.config.properties.CorsProperties;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.SignupRequest;
import com.boggle_boggle.bbegok.enums.SignStatus;
import com.boggle_boggle.bbegok.oauth.client.OAuth2RedirectUriBuilder;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.service.OAuth2LoginService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.service.UserService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import com.boggle_boggle.bbegok.utils.OauthValidateUtil;
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

    private final OAuth2LoginService oauth2LoginService;
    private final QueryService queryService;
    private final UserService userService;
    private final OAuth2RedirectUriBuilder oAuth2RedirectUriBuilder;
    private final CorsProperties corsProperties;

    //회원가입
    @PostMapping("/signup")
    public DataResponseDto<OAuthLoginResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        OAuthLoginResponse oauthLoginResponse = userService.signup(signupRequest.getPreSignupId(), signupRequest.getNickname(), signupRequest.getAgreements());
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }
        return DataResponseDto.of(oauthLoginResponse);
    }

    //리다이렉트할 인증서버URI를 리턴
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
    public DataResponseDto<OAuthLoginResponse> oauth2Callback(
            @PathVariable("provider") ProviderType providerType,
            @RequestParam("code") String code,
            @RequestParam(name="state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        OauthValidateUtil.validateState(request, state);
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(providerType, code, state);
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }
        //return DataResponseDto.of(oauthLoginResponse);

        //'auth'페이지로 리디렉션한다.
        HttpSession session = request.getSession(false);
        String redirectFront = (session != null)
                ? (String) session.getAttribute("redirect_front")
                : null;

        if (redirectFront == null ||
                corsProperties.getAllowedOrigins().lines().noneMatch(redirectFront::startsWith)) {
            response.sendError(400, "invalid front url");
        }

        if (session != null) session.removeAttribute("redirect_front");

        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            String frontUrl = UriComponentsBuilder
                    .fromUriString(redirectFront)   // https://app.bbaegok.store
                    .path("/auth")                    // /auth
                    .queryParam("status", r.isNew())     // ?new=true
                    .queryParam("preSignup", r.isNew())     // ?new=true
                    .build()
                    .toUriString();
        }

           // 인코딩까지 완료
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
