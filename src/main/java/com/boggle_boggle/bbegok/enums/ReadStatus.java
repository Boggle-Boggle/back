package com.boggle_boggle.bbegok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReadStatus {
    reading,
    pending,
    completed;
}
