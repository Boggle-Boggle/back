package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.entity.user.UserSettings;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.entity.UserPrincipal;
import com.boggle_boggle.bbegok.oauth.exception.OAuthProviderMissMatchException;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfoFactory;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.repository.user.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/** 사용자가 로그인을 시도할때 loadUser를 사용
 * 즉, Provider가 인증정보를 반환하면 스프링 시큐리티는 이 클래스를 사용하여 반환
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        User savedUser = userRepository.findByUserIdAndIsDeleted(userInfo.getId(), false);

        if (savedUser != null) { //서로 다른 인증제공자간 충돌을 방지
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            //가입한적 없다면 회원가입을 진행.
            savedUser = createUser(userInfo, providerType);
            userSettingsRepository.saveAndFlush(UserSettings.createUserSettings(savedUser));
        }

        //이미 가입한 유저라면 가입한 유저, 회원가입했다면 회원가입할 유저의 정보를 리턴
        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private void updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getEmail() != null && !user.getEmail().equals(userInfo.getEmail())) {
            user.updateEmail(userInfo.getEmail());
        }
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        User user = User.createUser(
                userInfo.getId(),
                providerType,
                userInfo.getEmail(),
                RoleType.GUEST
        );

        return userRepository.saveAndFlush(user);
    }
}
