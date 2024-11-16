package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class LibraryBook {
    private String title;
    private int page;

    public static LibraryBook fromEntity(Book book) {
        return LibraryBook.builder()
                .title(book.getTitle())
                .page(book.getPage())
                .build();
    }
}
