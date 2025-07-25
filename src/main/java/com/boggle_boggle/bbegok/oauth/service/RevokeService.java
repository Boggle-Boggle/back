package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.config.properties.OAuthProperties;
import com.boggle_boggle.bbegok.dto.request.WithdrawReasonRequest;
import com.boggle_boggle.bbegok.entity.WithdrawReason;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.client.AppleClientSecretGenerator;
import com.boggle_boggle.bbegok.repository.WithdrawReasonRepository;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import com.boggle_boggle.bbegok.service.QueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RevokeService {
    private final UserRepository userRepository;
    private final OAuthProperties oAuthProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final WithdrawReasonRepository WithdrawReasonRepository;
    private final QueryService queryService;
    private final AppleClientSecretGenerator appleClientSecretGenerator;

    @Transactional
    public void deleteAccount(String userSeq, WithdrawReasonRequest request) throws IOException {
        User user = queryService.getUser(userSeq);
        //탈퇴사유 저장 & User 삭제
        deleteUser(user, request);

        switch (user.getProviderType()) {
            case APPLE -> deleteAppleAccount(user);
            //case GOOGLE -> deleteGoogleAccount(user);
            //case KAKAO -> deleteKakaoAccount(user);
        }
    }

    private void deleteUser(User user, WithdrawReasonRequest request) {
        WithdrawReasonRepository.save(WithdrawReason.createWithdrawReason(user, request.getWithdrawType(), request.getWithdrawText()));
        user.softDelete();
        userRefreshTokenRepository.deleteByUser(user);
    }

    public void deleteAppleAccount(User user) throws IOException {
        String revokeUri = oAuthProperties.getApple().getRevokeUri();
        String data = appleClientSecretGenerator.buildRevokeBody(user.getOauth2RefreshToken());
        sendRevokeRequest(data, revokeUri, null);
    }

    public void deleteGoogleAccount(User user) {
        String revokeUri = oAuthProperties.getGoogle().getRevokeUri();
        String data = "token=" + user.getOauth2AccessToken();
        sendRevokeRequest(data, revokeUri, null);
    }

    public void deleteKakaoAccount(User user) {
        String revokeUri = oAuthProperties.getKakao().getRevokeUri();
        sendRevokeRequest(null, revokeUri, user.getOauth2AccessToken());
    }



    /**
     * @param data : revoke request의 body에 들어갈 데이터
     * @param revokeUrl : revoke처리할 uri
     * @param accessToken : 카카오의 경우 url이 아니라 헤더에 access token을 첨부해서 보내줘야 함
     */
    private void sendRevokeRequest(String data, String revokeUrl, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(data, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

        // 제공업체 정보를 포함한 간단한 로그 기록
        log.debug("소셜 회원 연결해제 요청 결과 [{}, {}]", statusCode, responseBody);
    }

}



