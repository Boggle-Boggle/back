package com.boggle_boggle.bbegok.oauth.info;

import java.util.Map;

/** 각 OAuth2에서 가져올 데이터들을 정의하는 추상클래스
 * 현재 빼곡의 경우 오직 식별자만 필요로 한다.
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
