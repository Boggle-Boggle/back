package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.dto.TermsAgreement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(max = 15)
    private String nickname;

    @Valid
    @NotEmpty
    List<TermsAgreement> agreements;
}
