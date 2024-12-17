package com.boggle_boggle.bbegok.utils;

public class SpecialCharUtil {
    public static String convertSpecialChars(String input) {
        if (input == null)return null;
        input = input.replace("&gt;", ">");
        input = input.replace("&lt;", "<");
        input = input.replace("&gt", ">");
        input = input.replace("&lt", "<");
        return input;
    }
}
