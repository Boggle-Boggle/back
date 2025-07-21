package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.enums.SignStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(staticName = "of")
@Getter
public class OAuthLoginResponse  {
    private SignStatus status;           // SIGNUP_REQUIRED or EXISTING_USER
    private Long preSignupId;  // 회원가입 필요한 경우 발급된 세션 ID
    private String accessToken;      // 기존회원이면 발급

    private String refreshToken;
    private String deviceCode;

    public static OAuthLoginResponse signupRequired(Long preSignupId) {
        return of(SignStatus.SIGNUP_REQUIRED, preSignupId, null, null,null);
    }

    public static OAuthLoginResponse existingUser(String accessToken, String refreshToken, String deviceCode) {
        return of(SignStatus.EXISTING_USER, null, accessToken, refreshToken, deviceCode);
    }

    public void clearLoginData() {
        this.refreshToken = null;
        deviceCode = null;
    }
}
