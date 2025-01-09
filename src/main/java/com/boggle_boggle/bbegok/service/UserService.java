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
        User user = userRepository.findByUserId(userId);
        if(user.getIsDeleted()) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
        return user;
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
        if(getUser(userId).getUserName() != null && getUser(userId).getUserName().equals(nickname)) return true;
        else return userRepository.findByUserName(nickname).isEmpty();
    }

    //약관동의 및 권한 업데이트
    public void agreeToTerms(List<TermsAgreement> termsAgreementList, String userId) {
        termsValid(termsAgreementList);
        //유효성검사 완료 = 요청이 현재약관에 대한 필수값을 만족하고있음.

        User user = getUser(userId);
        for(TermsAgreement ta : termsAgreementList) {
            Terms terms = termsJpaRepository.findById(ta.getId())
                    .orElseThrow(() -> new GeneralException(Code.TERMS_NOT_FOUND));

            //이미 동의한 기록이 있는데 요청값이 false이면 해당 기록을 삭제
            //이미 동의한 기록이 없는데 요청값이 true이면 해당 기록을 추가
            Optional<AgreeToTerms> optionalAgreeToTerms = agreeToTermsRepository.findByUserAndTerms(user, terms);
            optionalAgreeToTerms.ifPresentOrElse(
                    agreeToTerms -> { // 이전에 동의한 경우, false면 동의 기록 삭제
                        if (!ta.getIsAgree()) agreeToTermsRepository.delete(agreeToTerms);
                    },
                    () -> { // 이전에 동의하지 않은 경우, true면 동의기록 저장
                        if (ta.getIsAgree()) agreeToTermsRepository.save(AgreeToTerms.createAgreeToTerms(user, terms));
                    }
            );
        }

        //GUEST->USER로 변경
        String latestVersion = termsRepository.getLatestTermsVersion();
        user.updateGuestToUser(latestVersion);
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

    //현시점 필수약관에 동의하지 않으면 에러 전송/ 최신 필수약관에 모두 동의해야만 함.
    public void termsValid(List<TermsAgreement> termsAgreementList) {
        //현재 시점의 약관 로드
        String latestVersion = termsRepository.getLatestTermsVersion();
        List<Terms> latestTerms = termsJpaRepository.findByVersion(latestVersion);
        latestTerms.sort(Comparator.comparing(Terms::getTermsSeq));
        termsAgreementList.sort(Comparator.comparing(TermsAgreement::getId));

        //요청객체 내용이 현재 약관을 전부 포함하는지 확인
        if(latestTerms.size() == termsAgreementList.size()) {
            for(int i=0; i<latestTerms.size(); i++){
                if(!Objects.equals(latestTerms.get(i).getTermsSeq(), termsAgreementList.get(i).getId())) {
                    throw new GeneralException(Code.LATEST_TERMS_NOT_INCLUDED);
                }else if(latestTerms.get(i).getIsMandatory() && !termsAgreementList.get(i).getIsAgree()) {
                    //요청객체가 현재 약관에 포함되지만, 필수약관에 동의하지 않았을경우
                    throw new GeneralException(Code.REQUIRED_TERMS_NOT_AGREED);
                }
            }
        } else throw new GeneralException(Code.LATEST_TERMS_NOT_INCLUDED);
    }

}
