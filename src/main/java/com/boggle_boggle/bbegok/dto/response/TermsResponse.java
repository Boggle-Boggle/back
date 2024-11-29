package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.SearchLogs;
import com.boggle_boggle.bbegok.dto.Term;
import com.boggle_boggle.bbegok.entity.Terms;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TermsResponse {
    private String version;
    private List<Term> terms;

    public static TermsResponse from(String version, List<Terms> termsList) {
        return TermsResponse.builder()
                .version(version)
                .terms(
                        termsList.stream().map(
                                terms -> Term.builder()
                                        .id(terms.getTermsSeq())
                                        .title(terms.getTitle())
                                        .content(terms.getContent())
                                        .isMandatory(terms.getIsMandatory())
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }
}
