package com.boggle_boggle.bbegok.oauth.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final OAuthProperties properties;
    private final ECPrivateKey privateKey;

    public String createClientSecret() {
        OAuthProperties.AppleProvider apple = properties.getApple();

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600); // Apple은 최대 6개월, 우리는 1시간으로 제한

        return JWT.create()
                .withIssuer(apple.getTeamId())
                .withSubject(apple.getClientId())
                .withAudience(apple.getIss())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withKeyId(apple.getKeyId())
                .sign(Algorithm.ECDSA256(null, privateKey));
    }

    public String buildRevokeBody(String refreshToken) {
        OAuthProperties.AppleProvider apple = properties.getApple();
        return "client_id=" + apple.getClientId()
                + "&client_secret=" + createClientSecret()
                + "&token=" + refreshToken
                + "&token_type_hint=refresh_token";
    }
}
