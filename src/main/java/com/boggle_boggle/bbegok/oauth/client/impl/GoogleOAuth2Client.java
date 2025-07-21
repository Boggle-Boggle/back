package com.boggle_boggle.bbegok.oauth.client.impl;

import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2Client implements OAuth2ProviderClient {
    @Override
    public String requestAccessToken(String code) {
        return "";
    }

    @Override
    public OAuth2UserInfo requestUserInfo(String accessToken) {
        return null;
    }
}
