package com.boggle_boggle.bbegok.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth")
@Getter
@Setter
public class OAuthProperties {

    private Provider kakao;
    private Provider google;
    private AppleProvider apple;

    @Getter @Setter
    public static class Provider {
        private String clientId;  //aud
        private String clientSecret;
        private String redirectUri;
        private String tokenUri;
        private String authorizeUri;
        private String userInfoUri;
        private String scope;
        private String revokeUri;
    }


    @Getter
    @Setter
    public static class AppleProvider extends Provider {
        private String teamId;
        private String keyId;
        private String keyPath;
        private String iss;
        private String publicKeyUrl;
    }
}