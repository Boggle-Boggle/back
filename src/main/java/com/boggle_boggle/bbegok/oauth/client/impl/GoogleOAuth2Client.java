package com.boggle_boggle.bbegok.oauth.client.impl;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.client.response.GoogleTokenResponse;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.impl.GoogleOAuth2UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuth2Client implements OAuth2ProviderClient {

    private final WebClient.Builder webClientBuilder;
    private final OAuthProperties oAuthProperties;
    private final ObjectMapper objectMapper;
    private String cachedIdToken;  // id_token 보관

    @Override
    public String requestAccessToken(String code) {
        //구글은 access_token과 id_token을 한번에 받는다
        GoogleTokenResponse response = Objects.requireNonNull(webClientBuilder.build()
                .post()
                .uri(oAuthProperties.getGoogle().getTokenUri())
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", oAuthProperties.getGoogle().getClientId())
                        .with("client_secret", oAuthProperties.getGoogle().getClientSecret())
                        .with("redirect_uri", oAuthProperties.getGoogle().getRedirectUri())
                        .with("code", code))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block());

        cachedIdToken = response.getIdToken();  // id_token 저장
        return response.getAccessToken();
    }

    @Override
    public OAuth2UserInfo requestUserInfo(String accessToken) {
        Map<String, Object> attributes = decodeIdTokenToMap(cachedIdToken);
        return new GoogleOAuth2UserInfo(attributes);
    }

    private Map<String, Object> decodeIdTokenToMap(String idToken) {
        String[] parts = idToken.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to decode id_token", e);
        }
    }
}