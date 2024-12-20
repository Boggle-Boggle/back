package com.boggle_boggle.bbegok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReadStatus {
    reading("읽는 중인 책"),
    pending("읽고 있는 책"),
    completed("다 읽은 책");

    private final String libraryName;
}
