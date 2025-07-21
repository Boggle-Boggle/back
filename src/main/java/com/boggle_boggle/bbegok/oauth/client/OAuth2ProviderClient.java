package com.boggle_boggle.bbegok.oauth.client;

import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;

//인증서버로의 요청을 추상화
public interface OAuth2ProviderClient {
    String requestAccessToken(String code);
    OAuth2UserInfo requestUserInfo(String accessToken);
}