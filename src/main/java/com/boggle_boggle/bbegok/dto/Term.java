package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Terms;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class Term {
    private Long id;
    private int version;
    private String title;
    private String content;
    private boolean isMandatory;

    public static Term from(Terms terms) {
        return Term.builder()
                .id(terms.getTermsSeq())
                .version(terms.getVersion())
                .title(terms.getTitle())
                .content(terms.getContent())
                .isMandatory(terms.getIsMandatory())
                .build();
    }
}
