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
    private String isbn;
    private String title;
    private String author;
    private String cover;
    private String publisher;
    @JsonProperty("isAdult")
    private boolean adult;
    @JsonProperty("isMyBook")
    private boolean myBook;

    public BookData withMyBook(boolean myBook) {
        return BookData.builder()
                .title(this.title)
                .isbn(this.isbn)
                .author(this.author)
                .cover(this.cover)
                .publisher(this.publisher)
                .adult(this.adult)
                .myBook(myBook)
                .build();
    }
}
