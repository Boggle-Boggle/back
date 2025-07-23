package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.enums.SignStatus;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.service.OAuth2LoginService;
import com.boggle_boggle.bbegok.service.QueryService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2AuthController {

    private final OAuth2LoginService oauth2LoginService;
    private final QueryService queryService;

    //oauth 인증서버에서 인가코드를 리다이렉트(302)하는 콜백 API
    @GetMapping("/callback/{provider}")
    public DataResponseDto<OAuthLoginResponse> oauth2Callback(
            @PathVariable("provider") ProviderType providerType,
            @RequestParam("code") String code,
            @RequestParam(name="state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(providerType, code, state);
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }
        return DataResponseDto.of(oauthLoginResponse);
    }

    //APPLE(POST) 전용 콜백 API
    @PostMapping("/callback/apple")
    public DataResponseDto<OAuthLoginResponse> oauth2AppkeCallback(
            @RequestParam("code") String code,
            @RequestParam(name="state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {
        OAuthLoginResponse oauthLoginResponse = oauth2LoginService.processOAuth2Callback(ProviderType.APPLE, code, state);
        if(oauthLoginResponse.getStatus() == SignStatus.EXISTING_USER) {
            queryService.setLoginCookie(request, response, oauthLoginResponse);
            oauthLoginResponse.clearLoginData();
        }
        return DataResponseDto.of(oauthLoginResponse);
    }
}
