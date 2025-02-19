package com.boggle_boggle.bbegok.oauth.info.impl;

import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;

import java.util.Map;

/** Kakao OAuth정보로부터 가져올 데이터들을 재정의
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String,Object> kakaoAccount = (Map<String,Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {
            return null;
        }

        return (String) kakaoAccount.get("email");
    }
}
