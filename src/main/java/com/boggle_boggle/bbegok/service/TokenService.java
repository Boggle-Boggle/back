package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;

    public AuthToken createAccessToken(User user) {
        long expiry = appProperties.getAuth().getAccessTokenExpiry();
        Date expiryDate = new Date(System.currentTimeMillis() + expiry);
        return tokenProvider.createAuthToken(user, expiryDate);
    }

    public AuthToken createRefreshToken(User user) {
        long expiry = appProperties.getAuth().getRefreshTokenExpiry();
        Date expiryDate = new Date(System.currentTimeMillis() + expiry);
        return tokenProvider.createAuthToken(user, expiryDate);
    }
}