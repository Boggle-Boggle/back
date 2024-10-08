package com.boggle_boggle.bbegok.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class BookData {
    private String title;
    private String isbn;
    private String author;
    private LocalDateTime pubDate;
    private String cover;
    private String publisher;
}
