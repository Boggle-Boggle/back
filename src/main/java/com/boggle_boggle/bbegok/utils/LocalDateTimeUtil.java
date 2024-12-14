package com.boggle_boggle.bbegok.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static LocalDateTime StringToLocalDateAndAddTime(String date){
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return localDate.atStartOfDay();
    }

    public static boolean isStartBeforeEnd(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return startLocalDateTime.isBefore(endLocalDateTime) || startLocalDateTime.isEqual(endLocalDateTime);
    }

    public static LocalDateTime StringToLocalDate(String date){
        return LocalDateTime.parse(date, FORMATTER);
    }
    public static String getCurrentTimeAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
