package com.boggle_boggle.bbegok.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {
    @Getter
    @Setter
    public static class Auth {
        private String tokenUrl;
        private String publicKeyUrl;
    }

    private Auth auth = new Auth();
    private String teamId;
    private String keyId;
    private String keyPath;
    private String clientId;
    private String redirectUri;
    private String iss;
    private String aud;

    public String getAppleLoginUrl(String redirectUri) {
        return iss + "/auth/authorize"
                + "?client_id=" + keyId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }
}