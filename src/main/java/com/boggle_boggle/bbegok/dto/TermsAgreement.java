package com.boggle_boggle.bbegok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TermsAgreement {
    @NotNull
    private Long id;
    @NotNull
    private Boolean isAgree;

    public static TermsAgreement of(Long id, Boolean isAgree) {
        TermsAgreement agreement = new TermsAgreement();
        agreement.id = id;
        agreement.isAgree = isAgree;
        return agreement;
    }
}
