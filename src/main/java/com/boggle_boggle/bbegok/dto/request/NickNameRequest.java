package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NickNameRequest {
    @NotBlank
    @NotNull
    @Size(max = 12)
    private String nickname;
}
