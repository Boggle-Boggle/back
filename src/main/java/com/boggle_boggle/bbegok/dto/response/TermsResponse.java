package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.entity.Terms;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TermsResponse {
    private List<Term> terms;

    public static TermsResponse from(List<Terms> termList) {
        return TermsResponse.builder()
                .terms(termList.stream()
                        .map(Term::from)
                        .toList())
                .build();
    }
}
