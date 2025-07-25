package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.LibraryRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import com.boggle_boggle.bbegok.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class QueryService {
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;
    @Value("${bbaegok.root-domain}")
    private String domain;

    @Transactional(readOnly = true)
    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<Library> getLibraries(List<Long> idList) {
        List<Library> libraries = libraryRepository.findAllById(idList);
        if (libraries.size() != idList.size()) {
            throw new GeneralException(Code.LIBRARY_NOT_FOUND);
        }
        return libraries;
    }

    public void setLoginCookie(HttpServletRequest request, HttpServletResponse response, OAuthLoginResponse oauthLoginResponse) {
        int cookieMaxAge = CookieUtil.getMaxAgeByRefreshTokenExpiry(appProperties.getAuth().getRefreshTokenExpiry());
        String refreshToken = oauthLoginResponse.getRefreshToken();
        String deviceId = oauthLoginResponse.getDeviceCode();

        CookieUtil.clearAndAddCookie(request, response, REFRESH_TOKEN, refreshToken, cookieMaxAge, domain);
        CookieUtil.clearAndAddCookie(request, response, DEVICE_CODE, deviceId, cookieMaxAge, domain);
    }

    public void clearAllCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN, domain);
        CookieUtil.deleteCookie(request, response, DEVICE_CODE, domain);
    }
}
