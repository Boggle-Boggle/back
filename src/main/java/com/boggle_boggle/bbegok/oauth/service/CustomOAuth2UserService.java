package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.entity.UserPrincipal;
import com.boggle_boggle.bbegok.oauth.exception.OAuthProviderMissMatchException;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfoFactory;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
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
        String userId = user.getName(); //OAuth2에서 제공하는 고유식별자
        User savedUser = userRepository.findByUserId(userId);

        if (savedUser != null) { //서로 다른 인증제공자간 충돌을 방지
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
        } else savedUser = createUser(userId, providerType);




        //이미 가입한 유저라면 가입한 유저, 회원가입했다면 회원가입할 유저의 정보를 리턴
        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(String userId, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.createUser(
                userId,
                "Y",
                providerType,
                RoleType.GUEST,
                now,
                now
        );

        return userRepository.saveAndFlush(user);
    }
}
