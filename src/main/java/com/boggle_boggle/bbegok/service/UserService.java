package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.entity.AgreeToTerms;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.AgreeToTermsRepository;
import com.boggle_boggle.bbegok.repository.TermsJpaRepository;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final TermsJpaRepository termsJpaRepository;
    private final AgreeToTermsRepository agreeToTermsRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void updateNicName(String userId, String name) {
        User user = getUser(userId);
        user.updateNickName(name);
    }

    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByUserName(nickname).isEmpty();
    }

    //약관동의 및 권한 업데이트
    public void agreeToTerms(List<TermsAgreement> termsAgreementList, String userId) {
        termsValid(termsAgreementList); //우효성 검사부터

        User user = getUser(userId);
        for(TermsAgreement ta : termsAgreementList) {
            Terms terms = termsJpaRepository.findById(ta.getId())
                    .orElseThrow(() -> new GeneralException(Code.TERMS_NOT_FOUND));

            //이미 동의한 기록이 있는데 요청값이 false이면 해당 기록을 삭제
            //이미 동의한 기록이 없는데 요청값이 true이면 해당 기록을 추가
            Optional<AgreeToTerms> optionalAgreeToTerms = agreeToTermsRepository.findByUserAndTerms(user, terms);
            optionalAgreeToTerms.ifPresent(agreeToTerms -> {
                if(!ta.getIsAgree()) agreeToTermsRepository.delete(agreeToTerms);
            });
            if (optionalAgreeToTerms.isEmpty() && ta.getIsAgree()) {
                agreeToTermsRepository.save(AgreeToTerms.createAgreeToTerms(user, terms));
            }
        }
    }

    //최신 약관 조회(동의여부도 같이 전송)
    public TermsResponse getLatestTerms(String userId) {
        String latestVersion = termsRepository.getLatestTermsVersion();
        List<Terms> terms = termsJpaRepository.findByVersion(latestVersion);
        User user = getUser(userId);
        List<Term> termList = new ArrayList<>();

        for(Terms t : terms) {
            Optional<AgreeToTerms> agreeToTerms = agreeToTermsRepository.findByUserAndTerms(user, t);
            agreeToTerms.ifPresent(att -> {
                termList.add(Term.builder()
                        .id(t.getTermsSeq())
                        .title(t.getTitle())
                        .content(t.getContent())
                        .isMandatory(t.getIsMandatory())
                        .isAgree(true)
                        .build());
            });
            if (agreeToTerms.isEmpty()) {
                termList.add(Term.builder()
                        .id(t.getTermsSeq())
                        .title(t.getTitle())
                        .content(t.getContent())
                        .isMandatory(t.getIsMandatory())
                        .isAgree(false)
                        .build());
            }
        }

        return TermsResponse.from(latestVersion, termList);
    }

    //필수약관에 동의하지 않으면 에러 전송
    public void termsValid(List<TermsAgreement> termsAgreementList) {
        for(TermsAgreement ta : termsAgreementList) {
            Terms terms = termsJpaRepository.findById(ta.getId())
                    .orElseThrow(() -> new GeneralException(Code.TERMS_NOT_FOUND));
            if(terms.getIsMandatory() && !ta.getIsAgree()) {
                throw new GeneralException(Code.TERMS_NOT_AGREED);
            }
        }
    }
}
