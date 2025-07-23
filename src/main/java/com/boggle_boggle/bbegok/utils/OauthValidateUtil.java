package com.boggle_boggle.bbegok.utils;

import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;

public class OauthValidateUtil {
    public static void validateState(HttpServletRequest request, String state) {
        String expectedState = (String) request.getSession().getAttribute("oauth2_state");

        if (expectedState == null || !expectedState.equals(state)) {
            throw new GeneralException(Code.INVALID_STATE);
        }

        // 재사용 방지 위해 제거
        request.getSession().removeAttribute("oauth2_state");
    }

}
