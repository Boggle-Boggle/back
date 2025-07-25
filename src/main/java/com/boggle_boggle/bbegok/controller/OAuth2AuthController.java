package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
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
    public DataResponseDto<Map<String, String>> authorize(@RequestParam("provider") ProviderType providerType, HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth2_state", state);

        String redirectUrl = oAuth2RedirectUriBuilder.buildRedirectUri(providerType, state);
        return DataResponseDto.of(Map.of("redirectUrl", redirectUrl));
    }

    //oauth 인증서버에서 인가코드를 리다이렉트(302)하는 콜백 API
    @GetMapping("/oauth2/callback/{provider}")
    public DataResponseDto<OAuthLoginResponse> oauth2Callback(
            @PathVariable("provider") ProviderType providerType,
            @RequestParam("code") String code,
            @RequestParam(name="state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {
        OauthValidateUtil.validateState(request, state);
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(providerType, code, state);
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }
        return DataResponseDto.of(oauthLoginResponse);
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
        return DataResponseDto.of(oauthLoginResponse);
    }
}
