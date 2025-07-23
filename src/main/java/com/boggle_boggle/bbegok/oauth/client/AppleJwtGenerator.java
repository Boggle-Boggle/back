package com.boggle_boggle.bbegok.oauth.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleJwtGenerator {

    private final OAuthProperties oAuthProperties;
    private final ECPrivateKey privateKey;

    public String generate() {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600);

        return JWT.create()
                .withIssuer(oAuthProperties.getApple().getTeamId())
                .withSubject(oAuthProperties.getApple().getClientId())
                .withAudience(oAuthProperties.getApple().getIss())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withKeyId(oAuthProperties.getApple().getKeyId())
                .sign(Algorithm.ECDSA256(null, privateKey));
    }
}