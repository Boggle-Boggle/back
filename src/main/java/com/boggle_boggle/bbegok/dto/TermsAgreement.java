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
}
