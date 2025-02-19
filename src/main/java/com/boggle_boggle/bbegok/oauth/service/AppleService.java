package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.config.properties.AppleProperties;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.exception.OAuthProviderMissMatchException;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.repository.user.UserSettingsRepository;
import com.boggle_boggle.bbegok.service.AccessTokenService;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import com.boggle_boggle.bbegok.utils.UuidUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({ AppleProperties.class })
public class AppleService {
    private final AppleProperties appleProperties;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final AccessTokenService accessTokenService;
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public String getAppleLoginUrl(String redirectUri) {
        return appleProperties.getAppleLoginUrl(redirectUri);
    }

    public User process(String code) {
        User savedUser = null;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(appleProperties.generateAuthToken(code));
            String accessToken = String.valueOf(jsonObj.get("access_token"));
            String refreshToken  = String.valueOf(jsonObj.get("refresh_token"));

            // ID TOKEN을 통해 회원 고유 식별자 받기
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject payload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), JSONObject.class);

            String userId = String.valueOf(payload.get("sub"));
            String userEmail = String.valueOf(payload.get("email"));

            savedUser = userRepository.findByUserIdAndIsDeleted(userId, false);

            if (savedUser != null) { //서로 다른 인증제공자간 충돌을 방지
                if (ProviderType.APPLE != savedUser.getProviderType()) {
                    throw new OAuthProviderMissMatchException(
                            "Looks like you're signed up with " + ProviderType.APPLE +
                                    " account. Please use your " + savedUser.getProviderType() + " account to login."
                    );
                }
                savedUser.updateToken(accessToken, refreshToken);
                savedUser.updateEmail(userEmail);
            } else {
                savedUser = createAppleUser(userId, userEmail, accessToken, refreshToken);
                userSettingsRepository.saveAndFlush(UserSettings.createUserSettings(savedUser));
            }

            return savedUser;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse json data");
        } catch (IOException | java.text.ParseException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String loginSuccess(HttpServletRequest request, HttpServletResponse response, User user) {
        // access 토큰 설정 : GUEST는 그냥 저장, User의 경우 약관정보 확인 후 LIMITED_USER 또는 USER를 저장
        Date now = new Date();
        AuthToken accessToken = accessTokenService.createAccessToken(user, user.getRoleType(), now);

        // refresh 토큰 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // 디바이스코드 생성 및 리프레쉬토큰 DB에 저장
        String deviceId = UuidUtil.createUUID().toString();
        userRefreshTokenRepository.saveAndFlush(UserRefreshToken.createUserRefreshToken(user, refreshToken.getToken(), deviceId));

        //디바이스코드, 토큰을 쿠키에 저장
        saveCookie(response, request, DEVICE_CODE, refreshTokenExpiry, deviceId);
        saveCookie(response, request, REFRESH_TOKEN, refreshTokenExpiry, refreshToken.getToken());

        return accessToken.getToken();
    }

    public String determineSuccessRedirectUrl(String accessToken, String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("token", accessToken)
                .build().toUriString();
    }

    private User createAppleUser(String userId, String userEmail, String accessToken, String refreshToken) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.createUser(
                userId,
                ProviderType.APPLE,
                userEmail,
                RoleType.GUEST,
                accessToken,
                refreshToken
        );

        return userRepository.saveAndFlush(user);
    }

    protected void saveCookie(HttpServletResponse response, HttpServletRequest request, String cookieName, long tokenExpiry, String tokenValue) {
        int cookieMaxAge = (int) tokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, tokenValue, cookieMaxAge);
    }
}
