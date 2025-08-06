package com.boggle_boggle.bbegok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
public class TokenDto {
    String accessToken;
    String refreshToken;
    boolean isRefreshUpdated;
}
