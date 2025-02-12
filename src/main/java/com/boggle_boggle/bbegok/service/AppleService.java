package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.config.properties.AppleProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({ AppleProperties.class })
public class AppleService {
    private final AppleProperties appleProperties;

    public String getAppleLoginUrl(String redirectUri) {
        return appleProperties.getAppleLoginUrl(redirectUri);
    }

}
