package com.boggle_boggle.bbegok.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccessTokenResponse {
    private String accessToken;
}
