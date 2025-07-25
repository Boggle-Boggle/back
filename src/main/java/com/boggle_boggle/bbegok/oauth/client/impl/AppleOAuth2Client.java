package com.boggle_boggle.bbegok.oauth.client.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.oauth.client.AppleClientSecretGenerator;
import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.client.response.AppleTokenResponse;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.impl.AppleOAuth2UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuth2Client implements OAuth2ProviderClient {

    private final OAuthProperties oAuthProperties;
    private final ObjectMapper objectMapper;
    private final ECPrivateKey privateKey; //p8로 로그인용 JWT(액세스토큰) 개인키 만듦
    private final AppleClientSecretGenerator clientSecretGenerator;
    private String cachedIdToken;

    //콜백 code를 기반으로 access_token + id_token 발급
    @Override
    public String requestAccessToken(String code) {
        String clientSecret = clientSecretGenerator.createClientSecret();

        try {
            String rawResponse = WebClient.create()
                    .post()
                    .uri(oAuthProperties.getApple().getTokenUri())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("code", code)
                            .with("client_id", oAuthProperties.getApple().getClientId())
                            .with("client_secret", clientSecret)
                            .with("redirect_uri", oAuthProperties.getApple().getRedirectUri()))
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException("Apple 응답 오류")))
                    )
                    .bodyToMono(String.class)
                    .block();

            AppleTokenResponse tokenResponse = objectMapper.readValue(rawResponse, AppleTokenResponse.class);
            this.cachedIdToken = tokenResponse.getIdToken();
            return tokenResponse.getAccessToken();

        } catch (Exception e) {
            throw new RuntimeException("Apple OAuth 실패", e);
        }
    }



    //id_token 파싱하여 사용자 정보 추출
    @Override
    public OAuth2UserInfo requestUserInfo(String accessToken) {
        Map<String, Object> attributes = decodeIdToken(cachedIdToken);
        return new AppleOAuth2UserInfo(attributes);
    }


    //Base64 + JSON 파싱으로 user info 추출
    private Map<String, Object> decodeIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Apple ID Token 디코딩 실패", e);
        }
    }
}
