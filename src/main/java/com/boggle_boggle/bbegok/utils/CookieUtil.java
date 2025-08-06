package com.boggle_boggle.bbegok.utils;

import com.boggle_boggle.bbegok.dto.OAuthLoginResponse;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.DEVICE_CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@Slf4j
@Component
public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName()))  {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void clearAndAddCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, String domain) {
        deleteCookie(request, response, name, domain);
        addCookie(response, name, value, maxAge, domain);
    }

    public static int getMaxAgeByRefreshTokenExpiry(long refreshTokenExpiry) {
        return (int)(refreshTokenExpiry/1000L);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                //.domain(domain) - staging에선 도메인 제거
                //.sameSite("Lax") - staging에선 None사용
                .sameSite("None")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(true)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name, String domain) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                            .path("/")
                            //.domain(domain)
                            //.sameSite("Lax")
                            .sameSite("None")
                            .httpOnly(true)
                            .secure(true)
                            .maxAge(0)
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }

}
