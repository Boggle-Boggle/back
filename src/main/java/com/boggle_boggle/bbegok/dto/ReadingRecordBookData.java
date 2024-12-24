package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ReadingRecordBookData {
    private String cover;
    private String title;
    private String author;
    private String publisher;
    private LocalDateTime pubDate;
    private String genre;
    private String plot;

    public static ReadingRecordBookData fromEntity(Book book){
        return ReadingRecordBookData.builder()
                .cover(book.getImageUrl())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .pubDate(book.getPublishDate())
                .genre(book.getGenre())
                .plot(book.getPlot())
                .build();
    }
}
