package com.boggle_boggle.bbegok.repository.querydsl;

import com.boggle_boggle.bbegok.entity.QAgreeToTerms;
import com.boggle_boggle.bbegok.entity.QTerms;
import com.boggle_boggle.bbegok.entity.Terms;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.hibernate.query.results.Builders.fetch;

@RequiredArgsConstructor
@Repository
public class TermsQueryRepository {

    private final JPAQueryFactory queryFactory;

    //최신 약관 리스트 반환
    public List<Terms> findLatestTerms() {
        QTerms terms = QTerms.terms;

        //version 최대값 조회
        Integer maxVersion = queryFactory
                .select(terms.version.max())
                .from(terms)
                .fetchOne();

        //해당 version 값과 일치하는 약관 모두 조회
        return queryFactory
                .selectFrom(terms)
                .where(terms.version.eq(maxVersion))
                .fetch();
    }

    public boolean hasAgreedAllMandatoryLatestTerms(List<Long> agreedTermsIds) {
        QTerms terms = QTerms.terms;

        // 1. 최신 버전 필수 약관들의 termsSeq 리스트 조회
        List<Long> latestMandatoryTermsIds = queryFactory
                .select(terms.termsSeq)
                .from(terms)
                .where(
                        terms.isMandatory.isTrue(),
                        terms.version.eq(
                                JPAExpressions
                                        .select(terms.version.max())
                                        .from(terms)
                        )
                )
                .fetch();

        // 2. 사용자가 동의한 termsSeq 리스트가 최신 필수 약관ID를 모두 포함하고 있는지 검사
        return agreedTermsIds.containsAll(latestMandatoryTermsIds);
    }
}
