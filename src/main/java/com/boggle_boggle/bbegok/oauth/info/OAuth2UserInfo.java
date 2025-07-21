package com.boggle_boggle.bbegok.oauth.info;

import java.util.Map;

/** 각 OAuth2 Client에서 가져오는 userInfo를 서버 내부 표준으로 추상화
 */
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getEmail();

}
