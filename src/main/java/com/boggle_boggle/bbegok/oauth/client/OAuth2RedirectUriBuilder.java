package com.boggle_boggle.bbegok.oauth.client;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2RedirectUriBuilder {

    private final OAuthProperties oAuthProperties;

    public String buildRedirectUri(ProviderType provider, String state) {
        switch (provider) {
            case KAKAO:
                return buildKakaoUrl(state);
            case GOOGLE:
                return buildGoogleUrl(state);
            case APPLE:
                return buildAppleUrl(state);
            default:
                throw new IllegalArgumentException("지원되지 않는 로그인(" + provider+") 입니다.");
        }
    }

    private String buildKakaoUrl(String state) {
        OAuthProperties.Provider kakao = oAuthProperties.getKakao();
        return UriComponentsBuilder.fromHttpUrl(kakao.getAuthorizeUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakao.getClientId())
                .queryParam("redirect_uri", kakao.getRedirectUri())
                .queryParam("state", state)
                .build().toUriString();
    }

    private String buildGoogleUrl(String state) {
        OAuthProperties.Provider google = oAuthProperties.getGoogle();
        return UriComponentsBuilder.fromHttpUrl(google.getAuthorizeUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", google.getClientId())
                .queryParam("redirect_uri", google.getRedirectUri())
                .queryParam("scope", google.getScope())
                .queryParam("state", state)
                .build().toUriString();
    }

    private String buildAppleUrl(String state) {
        OAuthProperties.Provider apple = oAuthProperties.getApple();
        return UriComponentsBuilder.fromHttpUrl(apple.getAuthorizeUri())
                .queryParam("response_type", "code id_token")
                .queryParam("client_id", apple.getClientId())
                .queryParam("redirect_uri", apple.getRedirectUri())
                .queryParam("scope", apple.getScope())
                .queryParam("response_mode", "form_post")
                .queryParam("state", state)
                .build().toUriString();
    }
}
