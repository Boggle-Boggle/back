package com.boggle_boggle.bbegok.utils;

public class SpecialCharUtil {
    public static String convertSpecialChars(String input) {
        if (input == null) {
            return null;  // null 처리
        }
        // '&gt;'를 '>'로, '&lt;'를 '<'로 변경
        input = input.replace("&gt;", ">");
        input = input.replace("&lt;", "<");
        return input;
    }
}
