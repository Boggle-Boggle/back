package com.boggle_boggle.bbegok.oauth.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.boggle_boggle.bbegok.config.properties.oauth.AppleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleJwtGenerator {

    private final AppleProperties props;
    private final ECPrivateKey privateKey;
    private final AppleProperties appleProperties;

    public String generate() {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600);

        return JWT.create()
                .withIssuer(props.getTeamId())
                .withSubject(props.getClientId())
                .withAudience(appleProperties.getIss())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withKeyId(props.getKeyId())
                .sign(Algorithm.ECDSA256(null, privateKey));
    }
}