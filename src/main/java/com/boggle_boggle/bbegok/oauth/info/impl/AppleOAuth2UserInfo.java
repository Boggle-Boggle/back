package com.boggle_boggle.bbegok.oauth.info.impl;

import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) { super(attributes); }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("sub"));
    }
}