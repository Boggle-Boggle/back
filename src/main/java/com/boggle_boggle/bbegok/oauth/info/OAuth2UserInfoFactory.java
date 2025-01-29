package com.boggle_boggle.bbegok.oauth.info;

import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.info.impl.GoogleOAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.impl.KakaoOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
