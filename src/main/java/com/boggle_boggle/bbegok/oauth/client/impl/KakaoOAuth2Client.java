package com.boggle_boggle.bbegok.oauth.client.impl;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.oauth.client.response.KakaoTokenResponse;
import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.info.impl.KakaoOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

//카카오 인증서버와 통신(code <-> accesstoken <-> info)
@Component
@RequiredArgsConstructor
public class KakaoOAuth2Client implements OAuth2ProviderClient {

    private final WebClient.Builder webClientBuilder;
    private final OAuthProperties oAuthProperties;

    public String requestAccessToken(String code) {
        return Objects.requireNonNull(webClientBuilder.build()
                        .post()
                        .uri(oAuthProperties.getKakao().getTokenUri())
                        .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8")
                        .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                                .with("client_id", oAuthProperties.getKakao().getClientId())
                                .with("redirect_uri", oAuthProperties.getKakao().getRedirectUri())
                                .with("code", code)
                                .with("client_secret", oAuthProperties.getKakao().getClientSecret()))
                        .retrieve()
                        .bodyToMono(KakaoTokenResponse.class)
                        .block())
                .getAccessToken();
    }

    public KakaoOAuth2UserInfo requestUserInfo(String accessToken) {
        Map<String, Object> userAttributes = webClientBuilder.build()
                .get()
                .uri(oAuthProperties.getKakao().getUserInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return new KakaoOAuth2UserInfo(userAttributes);
    }
}
