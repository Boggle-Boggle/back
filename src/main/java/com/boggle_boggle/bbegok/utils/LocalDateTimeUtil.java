package com.boggle_boggle.bbegok.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {
    public static LocalDateTime StringToLocalDateAndAddTime(String date){
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return localDate.atStartOfDay();
    }
}
