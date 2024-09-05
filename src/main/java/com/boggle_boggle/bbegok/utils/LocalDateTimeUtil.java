package com.boggle_boggle.bbegok.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeUtil {
    public static LocalDateTime StringToLocalDateAndAddTime(String date){
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return localDate.atStartOfDay();
    }
    public static LocalDateTime StringToLocalDate(String date){
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public static LocalDateTime millisecondSStringToLocalDateTime(String dateTimeString) {
        String truncatedDate = dateTimeString.substring(0, dateTimeString.lastIndexOf('.'));
        return LocalDateTime.parse(truncatedDate);
    }
}
