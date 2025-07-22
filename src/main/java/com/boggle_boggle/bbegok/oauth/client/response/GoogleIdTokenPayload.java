package com.boggle_boggle.bbegok.oauth.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleIdTokenPayload {
    private String sub;
    private String email;
}
