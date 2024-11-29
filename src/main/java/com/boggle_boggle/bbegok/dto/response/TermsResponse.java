package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.entity.Terms;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TermsResponse {
    private String version;
    private List<Term> terms;

    public static TermsResponse from(String latestVersion, List<Term> termList) {
        return TermsResponse.builder()
                .version(latestVersion)
                .terms(termList)
                .build();
    }
}
