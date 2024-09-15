package com.boggle_boggle.bbegok.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class ReadDateDto {
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;
}
