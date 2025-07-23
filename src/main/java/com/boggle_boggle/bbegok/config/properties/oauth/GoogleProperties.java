package com.boggle_boggle.bbegok.config.properties.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "google")
public class GoogleProperties {
    private String redirectUri; //인증서버가 콜백할 백엔드 API
    private String clientId; //REST API 키(공개키)
    private String clientSecret; //토큰 발급 시, 보안을 강화
    private String tokenUri; //토큰 발급 시, 보안을 강화
}
