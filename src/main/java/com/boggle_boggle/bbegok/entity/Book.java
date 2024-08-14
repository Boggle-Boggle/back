package com.boggle_boggle.bbegok.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Column(name = "genre", length = 30)
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

    public static Book createBook(){
        return new Book();
    }
}
