package com.boggle_boggle.bbegok.oauth.client;

import com.boggle_boggle.bbegok.config.properties.oauth.AppleProperties;
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
    private final AppleProperties appleProperties;

    public AppleTokenResponse getToken(String code) {
        String clientSecret = jwtGenerator.generate();

        return WebClient.create()
                .post()
                .uri(appleProperties.getTokenUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("client_id", appleProperties.getClientId())
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", appleProperties.getRedirectUri()))
                .retrieve()
                .bodyToMono(AppleTokenResponse.class)
                .block();
    }
}
