package com.boggle_boggle.bbegok.oauth.handler;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfoFactory;
import com.boggle_boggle.bbegok.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.service.AccessTokenService;
import com.boggle_boggle.bbegok.service.TermsService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import com.boggle_boggle.bbegok.utils.UuidUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static com.boggle_boggle.bbegok.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AccessTokenService accessTokenService;
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /** 리다이렉트 URI결정 및 토큰 생성
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

        RoleType roleType = hasAuthority(authorities, RoleType.GUEST.getCode()) ? RoleType.GUEST : RoleType.USER;

        // access 토큰 설정 : GUEST는 그냥 저장, User의 경우 약관정보 확인 후 LIMITED_USER 또는 USER를 저장
        Date now = new Date();
        AuthToken accessToken = accessTokenService.createAccessToken(userInfo.getId(), roleType, now);

        // refresh 토큰 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // 디바이스코드 생성 및 리프레쉬토큰 DB에 저장
        String deviceId = UuidUtil.createUUID().toString();
        User userEntity = userRepository.findByUserIdAndIsDeleted(userInfo.getId(), false);
        userRefreshTokenRepository.saveAndFlush(UserRefreshToken.createUserRefreshToken(userEntity, refreshToken.getToken(), deviceId));

        //디바이스코드, 토큰을 쿠키에 저장
        saveCookie(response, request, DEVICE_CODE, refreshTokenExpiry, deviceId);
        saveCookie(response, request, REFRESH_TOKEN, refreshTokenExpiry, refreshToken.getToken());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    protected void saveCookie(HttpServletResponse response, HttpServletRequest request, String cookieName, long tokenExpiry, String tokenValue) {
        int cookieMaxAge = (int) tokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, tokenValue, cookieMaxAge);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
