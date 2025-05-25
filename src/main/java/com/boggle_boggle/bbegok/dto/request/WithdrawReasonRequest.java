package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.enums.WithdrawType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WithdrawReasonRequest {

    @NotNull
    private WithdrawType withdrawType;

    @Size(max = 400)
    private String withdrawText;
}