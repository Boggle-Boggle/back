package com.boggle_boggle.bbegok.oauth.client.impl;

import com.boggle_boggle.bbegok.config.properties.oauth.AppleProperties;
import com.boggle_boggle.bbegok.oauth.client.AppleTokenClient;
import com.boggle_boggle.bbegok.oauth.client.AppleUserInfoClient;
import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.client.response.AppleTokenResponse;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.impl.AppleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleOAuth2Client implements OAuth2ProviderClient {

    private final AppleTokenClient appleTokenClient;  // 애플은 비동기/JWT 토큰 생성 등 별도 로직 필요
    private final AppleUserInfoClient appleUserInfoClient;
    private String cachedIdToken; // id_token 저장

    @Override
    public String requestAccessToken(String code) {
        AppleTokenResponse tokenResponse = appleTokenClient.getToken(code);
        this.cachedIdToken = tokenResponse.getIdToken();
        return tokenResponse.getAccessToken();
    }

    @Override
    public OAuth2UserInfo requestUserInfo(String accessToken) {
        Map<String, Object> attributes = appleUserInfoClient.getUserInfo(cachedIdToken);
        return new AppleOAuth2UserInfo(attributes);
    }
}
