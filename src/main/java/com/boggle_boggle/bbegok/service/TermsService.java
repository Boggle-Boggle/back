package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.repository.AgreeToTermsRepository;
import com.boggle_boggle.bbegok.repository.TermsRepository;
import com.boggle_boggle.bbegok.repository.querydsl.TermsQueryRepository;
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
    private final TermsQueryRepository termsQueryRepository;

    public String getLatestAgreedTermsVersion(String userId) {
        return agreeToTermsRepository.findLatestAgreedTermsVersionByUserId(userId)
                .flatMap(versions -> versions.isEmpty() ? Optional.empty() : Optional.of(versions.get(0)))
                .orElse(null);
    }

    public TermsResponse getLatestTerms() {
        List<Terms> termsEntityList = termsQueryRepository.findLatestTerms();
        return TermsResponse.from(termsEntityList);
    }
}