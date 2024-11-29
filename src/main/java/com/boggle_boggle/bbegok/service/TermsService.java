package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.repository.AgreeToTermsRepository;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TermsService {
    private final AgreeToTermsRepository agreeToTermsRepository;

    //가장 최근에 동의한 약관의 버전을 리턴
    public String getLatestAgreedTermsVersion(String userId) {
        return agreeToTermsRepository.findLatestAgreedTermsVersionByUserId(userId)
                .flatMap(versions -> versions.isEmpty() ? Optional.empty() : Optional.of(versions.get(0)))
                .orElse(null);
    }

}