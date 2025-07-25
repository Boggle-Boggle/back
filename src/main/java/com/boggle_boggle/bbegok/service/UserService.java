package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.dto.TermsAgreement;
import com.boggle_boggle.bbegok.dto.response.TermsResponse;
import com.boggle_boggle.bbegok.entity.AgreeToTerms;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.entity.user.PreSignup;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.repository.AgreeToTermsRepository;
import com.boggle_boggle.bbegok.repository.TermsRepository;
import com.boggle_boggle.bbegok.repository.PreSignupRepository;
import com.boggle_boggle.bbegok.repository.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import com.boggle_boggle.bbegok.repository.querydsl.TermsQueryRepository;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final TermsQueryRepository termsQueryRepository;
    private final AgreeToTermsRepository agreeToTermsRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PreSignupRepository preSignupRepository;
    private final OAuth2LoginService oauth2LoginService;

    //회원가입
    @Transactional
    public OAuthLoginResponse signup(Long preSignupId, String nickname, List<TermsAgreement> agreements) {
        //(1) preSignupId가 유효한지부터 검사
        PreSignup preSignup = preSignupRepository
                .findById(preSignupId)
                .orElseThrow(() -> new GeneralException(Code.SIGNUP_NOTFOUND));
        if(!LocalDateTimeUtil.isStartBeforeEnd(LocalDateTime.now(), preSignup.getExpiresAt())) throw new GeneralException(Code.SIGNUP_EXPIRED);

        //(2) 최신 필수 약관에 모두 동의하고 있는지 검사
        boolean isMandatoryTrue = termsQueryRepository.hasAgreedAllMandatoryLatestTerms(agreements.stream()
                .filter(TermsAgreement::getIsAgree)
                .map(TermsAgreement::getId)
                .toList());
        if(!isMandatoryTrue) throw new GeneralException(Code.REQUIRED_TERMS_NOT_AGREED);

        //(3) 닉네임 중복검사, 중복가입 검사
        if(userRepository.existsByUserName(nickname)) throw new GeneralException(Code.DUPLICATE_USERNAME);
        if(userRepository.existsByUserIdAndIsDeletedFalse(preSignup.getOauth2Id())) throw new GeneralException(Code.ALREADY_SIGNEDUP);

        //(4) 새로운 user 및 약관동의 저장
        User user = userRepository.saveAndFlush(User.from(preSignup, nickname));
        saveAgreedTerms(user, agreements);

        //(5) 액세스토큰/리프레쉬토큰 세팅
        return oauth2LoginService.handleLogin(user);
    }

    @Transactional
    public void saveAgreedTerms(User user, List<TermsAgreement> agreements) {
        List<Long> agreedTermsIds = agreements.stream()
                .filter(TermsAgreement::getIsAgree)
                .map(TermsAgreement::getId)
                .toList();

        List<Terms> termsList = termsRepository.findAllById(agreedTermsIds);

        List<AgreeToTerms> agreeToTermsList = termsList.stream()
                .map(terms -> AgreeToTerms.of(user, terms))
                .toList();

        agreeToTermsRepository.saveAll(agreeToTermsList);
    }


//    public User getUser(String userSeq) {
//        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
//        if(user == null) {
//            //탈퇴한 적 있는 회원
//            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
//            else throw new GeneralException(Code.USER_NOT_FOUND);
//        }
//        return user;
//    }
//
//    public void updateNicName(String userSeq, String name) {
//        if(!isNicknameAvailable(userSeq, name)) throw new GeneralException(Code.BAD_REQUEST);
//        name = name.strip();
//        User user = getUser(userSeq);
//        user.updateNickName(name);
//    }
//
//    public boolean isNicknameAvailable(String userSeq, String nickname) {
//        if(nickname == null) return false;
//
//        nickname = nickname.strip();
//        if(nickname.length()>12 || nickname.isEmpty()) return false;
//
//        if(getUser(userSeq).getUserName() != null && getUser(userSeq).getUserName().equals(nickname)) return true;
//        else return userRepository.findByUserName(nickname).isEmpty();
//    }
//
//    public String getAuthorization(String userSeq) {
//        User user = getUser(userSeq);
//        RoleType role = user.getRoleType();
//        return role.toString();
//    }
//
//    //약관동의 및 권한 업데이트
//    public void agreeToTerms(List<TermsAgreement> termsAgreementList, String userSeq) {
//        termsValid(termsAgreementList);
//        //유효성검사 완료 = 요청이 현재약관에 대한 필수값을 만족하고있음.
//
//        User user = getUser(userSeq);
//        for(TermsAgreement ta : termsAgreementList) {
//            Terms terms = termsRepository.findById(ta.getId())
//                    .orElseThrow(() -> new GeneralException(Code.TERMS_NOT_FOUND));
//
//            //이미 동의한 기록이 있는데 요청값이 false이면 해당 기록을 삭제
//            //이미 동의한 기록이 없는데 요청값이 true이면 해당 기록을 추가
//            Optional<AgreeToTerms> optionalAgreeToTerms = agreeToTermsRepository.findByUserAndTerms(user, terms);
//            optionalAgreeToTerms.ifPresentOrElse(
//                    agreeToTerms -> { // 이전에 동의한 경우, false면 동의 기록 삭제
//                        if (!ta.getIsAgree()) agreeToTermsRepository.delete(agreeToTerms);
//                    },
//                    () -> { // 이전에 동의하지 않은 경우, true면 동의기록 저장
//                        if (ta.getIsAgree()) agreeToTermsRepository.save(AgreeToTerms.createAgreeToTerms(user, terms));
//                    }
//            );
//        }
//
//        //GUEST->USER로 변경
//        String latestVersion = termsRepository.getLatestTermsVersion();
//        user.updateGuestToUser(latestVersion);
//    }
//
//    //최신 약관 조회(동의여부도 같이 전송)
//    public TermsResponse getLatestTerms(String userSeq) {
//        String latestVersion = termsRepository.getLatestTermsVersion();
//        List<Terms> terms = termsRepository.findByVersion(latestVersion);
//        User user = getUser(userSeq);
//        List<Term> termList = new ArrayList<>();
//
//        for(Terms t : terms) {
//            Optional<AgreeToTerms> agreeToTerms = agreeToTermsRepository.findByUserAndTerms(user, t);
//            agreeToTerms.ifPresent(att -> {
//                termList.add(Term.builder()
//                        .id(t.getTermsSeq())
//                        .title(t.getTitle())
//                        .content(t.getContent())
//                        .isMandatory(t.getIsMandatory())
//                        .isAgree(true)
//                        .build());
//            });
//            if (agreeToTerms.isEmpty()) {
//                termList.add(Term.builder()
//                        .id(t.getTermsSeq())
//                        .title(t.getTitle())
//                        .content(t.getContent())
//                        .isMandatory(t.getIsMandatory())
//                        .isAgree(false)
//                        .build());
//            }
//        }
//
//        return TermsResponse.from(latestVersion, termList);
//    }
//
//    //현시점 필수약관에 동의하지 않으면 에러 전송/ 최신 필수약관에 모두 동의해야만 함.
//    public void termsValid(List<TermsAgreement> termsAgreementList) {
//        //현재 시점의 약관 로드
//        String latestVersion = termsRepository.getLatestTermsVersion();
//        List<Terms> latestTerms = termsRepository.findByVersion(latestVersion);
//        latestTerms.sort(Comparator.comparing(Terms::getTermsSeq));
//        termsAgreementList.sort(Comparator.comparing(TermsAgreement::getId));
//
//        //요청객체 내용이 현재 약관을 전부 포함하는지 확인
//        if(latestTerms.size() == termsAgreementList.size()) {
//            for(int i=0; i<latestTerms.size(); i++){
//                if(!Objects.equals(latestTerms.get(i).getTermsSeq(), termsAgreementList.get(i).getId())) {
//                    throw new GeneralException(Code.LATEST_TERMS_NOT_INCLUDED);
//                }else if(latestTerms.get(i).getIsMandatory() && !termsAgreementList.get(i).getIsAgree()) {
//                    //요청객체가 현재 약관에 포함되지만, 필수약관에 동의하지 않았을경우
//                    throw new GeneralException(Code.REQUIRED_TERMS_NOT_AGREED);
//                }
//            }
//        } else throw new GeneralException(Code.LATEST_TERMS_NOT_INCLUDED);
//    }

}
