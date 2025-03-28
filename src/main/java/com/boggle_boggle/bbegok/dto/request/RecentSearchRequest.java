package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RecentSearchRequest {
    @NotNull
    @Size(min = 1, max = 100)
    private String keyword;
}
