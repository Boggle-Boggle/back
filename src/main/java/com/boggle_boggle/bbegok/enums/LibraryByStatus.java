package com.boggle_boggle.bbegok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LibraryByStatus {
    reading("읽는 중인 책"),
    pending("읽고 싶은 책"),
    completed("다 읽은 책"),
    all("전체보기");

    private final String libraryName;
}
