package com.boggle_boggle.bbegok.oauth.repository;

import com.boggle_boggle.bbegok.utils.CookieUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/** OAuth 인증 요청을 쿠키를 통해 저장하고 관리한다.
 */
@Slf4j
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public final static String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public final static String REFRESH_TOKEN = "refresh_token";
    private final static int cookieExpireSeconds = 600;
    @Value("${bbaegok.root-domain}")
    private String domain;

    //요청의 쿠키에서 OAuth2 인증 요청을 불러온다
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    //OAuth2 인증 요청 객체를 쿠키에 저장
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("[테스트] domain 주입 : {}", domain);
        if (authorizationRequest == null) {
            CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, domain);
            CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME, domain);
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN, domain);
            return;
        }

        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), cookieExpireSeconds, domain);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds, domain);
        }
    }

    //OAuth2 인증 요청을 쿠키에서 제거
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    //모든 관련 쿠키를 제거
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, domain);
        CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME, domain);
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN, domain);
    }
}