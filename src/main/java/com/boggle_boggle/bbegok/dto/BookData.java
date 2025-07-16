package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isAdult")
    private boolean adult;
}
