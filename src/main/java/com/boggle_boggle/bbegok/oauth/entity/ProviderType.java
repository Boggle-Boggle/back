package com.boggle_boggle.bbegok.oauth.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProviderType {
    KAKAO,
    GOOGLE,
    APPLE;

    //요청바인딩시 소문자를 대문자로 변경
    @JsonCreator
    public static ProviderType from(String value) {
        return Arrays.stream(ProviderType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 ProviderType: " + value));
    }
}
