package com.boggle_boggle.bbegok.config.properties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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