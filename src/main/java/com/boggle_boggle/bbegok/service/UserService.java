package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.entity.AgreeToTerms;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.repository.AgreeToTermsRepository;
import com.boggle_boggle.bbegok.repository.TermsJpaRepository;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final TermsJpaRepository termsJpaRepository;
    private final AgreeToTermsRepository agreeToTermsRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    //Soft Delete를 위해 User컬럼 업데이트 및 토큰DB 삭제처리
    public void deleteUser(String userId) {
        User user = getUser(userId);
        user.softDelete();
        userRefreshTokenRepository.deleteByUser(user);
    }

    public void updateNicName(String userId, String name) {
        User user = getUser(userId);
        user.updateNickName(name);
    }

    public boolean isNicknameAvailable(String userId, String nickname) {
        if(getUser(userId).getUserName().equals(nickname)) return true;
        else return userRepository.findByUserName(nickname).isEmpty();
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

        //현재 필수약관에 모두 동의한 상태라면 GUEST->USER로 변경
        user.updateRoleType(RoleType.USER);
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

    //필수약관에 동의하지 않으면 에러 전송/ 최신 필수약관에 모두 동의해야만 함.
    public void termsValid(List<TermsAgreement> termsAgreementList) {
        String latestVersion = termsRepository.getLatestTermsVersion();
        List<Terms> latestTerms = termsJpaRepository.findByVersion(latestVersion);
        Collections.sort(latestTerms, (o1, o2) -> {
            return o1.getTermsSeq().compareTo(o2.getTermsSeq());
        });
        Collections.sort(termsAgreementList, (o1, o2) -> {
            return o1.getId().compareTo(o2.getId());
        });

        if(latestTerms.size() == termsAgreementList.size()) {
            for(int i=0; i<latestTerms.size(); i++){
                if(latestTerms.get(i).getTermsSeq() != termsAgreementList.get(i).getId()) throw new GeneralException(Code.LATEST_TERMS_NOT_AGREED);
            }
        } else throw new GeneralException(Code.LATEST_TERMS_NOT_AGREED);


        for(TermsAgreement ta : termsAgreementList) {
            Terms terms = termsJpaRepository.findById(ta.getId())
                    .orElseThrow(() -> new GeneralException(Code.TERMS_NOT_FOUND));
            if(terms.getIsMandatory() && !ta.getIsAgree()) {
                throw new GeneralException(Code.TERMS_NOT_AGREED);
            }
        }
    }

}
