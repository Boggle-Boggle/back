package com.boggle_boggle.bbegok.interceptor;

import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.token.AuthToken;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.service.TermsService;
import com.boggle_boggle.bbegok.utils.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgreementVersionInterceptor implements HandlerInterceptor {

    private final TermsRepository termsRepository; // Redis에서 최신 약관 버전 확인을 위한 서비스
    private final AuthTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("#Come In Interceptor");
        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);

        //약관검증
        String termsAgreedVersion = token.getTokenClaimsTermsVersion();
        log.debug("#recent agreed version : {}", termsAgreedVersion);
        log.debug("#recent Updated version : {}",termsRepository.getLatestTermsVersion());

        if (termsAgreedVersion == null || termsAgreedVersion.isEmpty()) {
            throw new GeneralException(Code.TOKEN_TERMS_NOT_FOUND);
        }

        //업데이트된 약관에 동의하지 않았다면,
        if(!termsAgreedVersion.equals(termsRepository.getLatestTermsVersion())) {
            throw new GeneralException(Code.LATEST_AGREEMENT_NOT_ACCEPTED);
        }

        return true; // 계속해서 컨트롤러로 요청을 넘김
    }

}

