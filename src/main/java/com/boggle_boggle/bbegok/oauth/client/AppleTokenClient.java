package com.boggle_boggle.bbegok.oauth.client;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.oauth.client.response.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AppleTokenClient {

    private final AppleJwtGenerator jwtGenerator;
    private final OAuthProperties oAuthProperties;

    public AppleTokenResponse getToken(String code) {
        String clientSecret = jwtGenerator.generate();

        return WebClient.create()
                .post()
                .uri(oAuthProperties.getApple().getTokenUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("client_id", oAuthProperties.getApple().getClientId())
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", oAuthProperties.getApple().getRedirectUri()))
                .retrieve()
                .bodyToMono(AppleTokenResponse.class)
                .block();
    }
}
