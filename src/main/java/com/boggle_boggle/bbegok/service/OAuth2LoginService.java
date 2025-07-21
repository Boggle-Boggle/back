package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.entity.user.PreSignup;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserRefreshToken;
import com.boggle_boggle.bbegok.oauth.client.OAuth2ProviderClient;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.repository.PreSignupRepository;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import com.boggle_boggle.bbegok.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginService {

    private final UserRepository userRepository;
    private final PreSignupRepository preSignupRepository;
    private final TokenService tokenService;
    private final Map<ProviderType, OAuth2ProviderClient> providerClients;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    //리다이렉션된 인가코드 정보를 바탕으로 인증서버에 토큰 요청 -> 토큰으로 정보요청 -> 로그인/회원가입
    @Transactional
    public OAuthLoginResponse processOAuth2Callback(ProviderType providerType, String code, String state) {
        //0. Provider별 client 세팅
        OAuth2ProviderClient client = providerClients.get(providerType);
        //1. 인가코드(code)로 액세스토큰 발급
        String oauthAccessToken = client.requestAccessToken(code);

        //2. 토큰(액세스토큰)으로 인증서버에 사용자 정보 요청
        OAuth2UserInfo userInfo = client.requestUserInfo(oauthAccessToken);
        String oauth2Id = userInfo.getId();
        String email = userInfo.getEmail();

        //3. 구분ID 기준으로 로그인/회원가입 분기
        User user = userRepository.findByUserIdAndIsDeleted(oauth2Id, false);
        if (user == null) {
            return handleSignup(oauth2Id, email, providerType);
        } else {
            return handleLogin(user);
        }
    }

    //PreSignUp에 정보 임시저장 후 pk만 리턴
    private OAuthLoginResponse handleSignup(String oauth2Id, String email, ProviderType providerType) {
        PreSignup preSignup = preSignupRepository.saveAndFlush(
                PreSignup.createPreSignup(oauth2Id, email, providerType)
        );
        return OAuthLoginResponse.signupRequired(preSignup.getPreSignupSeq());
    }

    //액세스토큰, 리프레쉬토큰, 디바이스코드 생성하여 리턴(재사용됨)
    public OAuthLoginResponse handleLogin(User user) {
        String accessToken = tokenService.createAccessToken(user).getToken();
        String refreshToken = tokenService.createRefreshToken(user).getToken();
        String deviceCode = UuidUtil.createUUID().toString();

        user.updateLoginAt(LocalDateTime.now());
        userRefreshTokenRepository.saveAndFlush(
                UserRefreshToken.createUserRefreshToken(user, refreshToken, deviceCode)
        );

        return OAuthLoginResponse.existingUser(accessToken, refreshToken, deviceCode);
    }
}
