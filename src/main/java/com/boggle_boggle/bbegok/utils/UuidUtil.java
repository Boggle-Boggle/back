package com.boggle_boggle.bbegok.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.UUID;

public class UuidUtil {
    public static UUID createUUID() {
        return UUID.randomUUID();
    }
}
