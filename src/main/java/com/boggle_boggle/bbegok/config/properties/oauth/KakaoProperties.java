package com.boggle_boggle.bbegok.config.properties.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    private String redirectUri; //인증서버가 콜백할 백엔드 API
    private String clientId; //REST API 키(공개키)
    private String tokenUri; //액세스토큰 요청할 인증서버 API
    private String userInfoUri; //유저정보 요청할 인증서버 API
    private String clientSecret; //토큰 발급 시, 보안을 강화
}
