package com.boggle_boggle.bbegok.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermsAgreement {
    private Long id;
    private Boolean isAgree;
}
