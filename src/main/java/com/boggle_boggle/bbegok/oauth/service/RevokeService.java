package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.entity.ProviderType;
import com.boggle_boggle.bbegok.repository.WithdrawReasonRepository;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RevokeService {
    private final UserRepository userRepository;
    private final OAuthProperties oAuthProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final WithdrawReasonRepository WithdrawReasonRepository;

    //@Value("${apple.revoke-url}")
    String appleRevokeUrl;
    //@Value("${google.revoke-url}")
    String googleRevokeUrl;
    //@Value("${kakao.revoke-url}")
    String kakaoRevokeUrl;

    //(0) 탈퇴사유 저장
    //(1) User 삭제
    //(2) 인증서버의 액세스토큰 무효화
    //(3) 쿠키제거

//    public void deleteAccount(String userSeq, WithdrawReasonRequest request) throws IOException {
//        User user = getUser(userSeq);
//
//        //탈퇴사유 저장
//        WithdrawReasonRepository.save(new WithdrawReason(user, request.getWithdrawType(), request.getWithdrawText()));
//
//        //User삭제
//        deleteUser(user);
//
//        switch (user.getProviderType()) {
//            case APPLE -> deleteAppleAccount(user);
//            //case GOOGLE -> deleteGoogleAccount(user);
//            //case KAKAO -> deleteKakaoAccount(user);
//        }
//    }

//    public void deleteAppleAccount(User user) throws IOException {
//        String data = appleProperties.getAppleRevokeData(user.getOauth2RefreshToken());
//        sendRevokeRequest(data, ProviderType.APPLE, null);
//    }

    public void deleteGoogleAccount(User user) {
        String data = "token=" + user.getOauth2AccessToken();
        sendRevokeRequest(data, ProviderType.GOOGLE, null);
    }

    public void deleteKakaoAccount(User user) {
        sendRevokeRequest(null, ProviderType.KAKAO, user.getOauth2AccessToken());
    }


    private void deleteUser(User user) {
        user.softDelete();
        userRefreshTokenRepository.deleteByUser(user);
    }

    /**
     * @param data : revoke request의 body에 들어갈 데이터
     * @param provider : oauth2 업체
     * @param accessToken : 카카오의 경우 url이 아니라 헤더에 access token을 첨부해서 보내줘야 함
     */
    private void sendRevokeRequest(String data, ProviderType provider, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String revokeUrl = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        switch (provider) {
            case APPLE -> revokeUrl = appleRevokeUrl;
            case GOOGLE -> revokeUrl = googleRevokeUrl;
            case KAKAO -> {
                revokeUrl = kakaoRevokeUrl;
                headers.setBearerAuth(accessToken);
            }
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

        // 제공업체 정보를 포함한 간단한 로그 기록
        log.debug("[{}] 소셜 회원 연결해제 요청 결과", provider.name());
        log.debug("[{}] Status Code: {}", provider.name(), statusCode);
        log.debug("[{}] Response: {}", provider.name(), responseBody);
    }

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }
}



