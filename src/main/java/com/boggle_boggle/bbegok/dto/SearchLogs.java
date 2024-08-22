package com.boggle_boggle.bbegok.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class SearchLogs {
    private String keyword;
    private LocalDateTime createdAt;
}
