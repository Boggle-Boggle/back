package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.info.OAuth2UserInfo;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final TermsRepository termsRepository; // Redis에서 최신 약관 버전 확인을 위한 서비스

    public AuthToken createAccessToken(String userId, RoleType roleType, Date now) {
        AuthToken accessToken;
        if(roleType == RoleType.GUEST) {
            accessToken = tokenProvider.createAuthToken(
                    userId,
                    roleType.getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
            );
        } else { //roleType == RoleType.USER -> 해당 user가 최신약관의 필수항목에 모두 동의하지 않았다면 LIMITED_USER를 반환함
            User userEntity = userRepository.findByUserIdAndIsDeleted(userId, false);
            String recentUpdatedVersion = termsRepository.getLatestTermsVersion();

            RoleType newRoleType;
            if(userEntity.getAgreedVersion()!=null && userEntity.getAgreedVersion().equals(recentUpdatedVersion)) newRoleType = RoleType.USER;
            else newRoleType = RoleType.LIMITED_USER;

            accessToken = tokenProvider.createAuthToken(
                    userId,
                    newRoleType.getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
            );
        }

        return accessToken;
    }
}
