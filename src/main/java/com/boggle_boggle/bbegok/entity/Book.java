package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter @ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookSeq;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "author", length = 30, nullable = false)
    private String author;

    @Column(name = "publisher", length = 30, nullable = false)
    private String publisher;

    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "genre", length = 100)
    private String genre;

    @Column(name = "plot", length = 255, nullable = false)
    private String plot;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "page")
    private Integer page;

    @Column(name = "publish_date", nullable = false)
    private LocalDateTime publishDate;

    protected Book(){}

    public Book(String isbn, String title, String author, String publisher,
                String cover, String genre, String plot, LocalDateTime pubDate, int page) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.imageUrl = cover;
        this.genre = genre;
        this.plot = plot;
        this.publishDate = pubDate;
        this.page = page;
    }

    public static Book createBook(BookDetailResponse bookData){
        return new Book(
                bookData.getIsbn(),
                bookData.getTitle(),
                bookData.getAuthor(),
                bookData.getPublisher(),
                bookData.getCover(),
                bookData.getGenre(),
                bookData.getPlot(),
                bookData.getPubDate(),
                bookData.getPage()
        );
    }
}
