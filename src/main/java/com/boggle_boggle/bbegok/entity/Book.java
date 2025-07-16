package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.entity.user.User;
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

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "author", length = 255, nullable = false)
    private String author;

    @Column(name = "publisher", length = 255)
    private String publisher;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Column(name = "genre", length = 200)
    private String genre;

    @Column(name = "plot", length = 1000)
    private String plot;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "page")
    private Integer page;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Column(name = "is_adult", nullable = false)
    private boolean isAdult;

    @Column(name = "is_custom", nullable = false)
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_seq")
    private User createdByUser;

    protected Book(){}

    protected Book(String isbn, String title, String author, String publisher,
                   String cover, String genre, String plot, LocalDateTime pubDate, int page, boolean isAdult) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.imageUrl = cover;
        this.genre = genre;
        this.plot = plot;
        this.publishDate = pubDate;
        this.page = page;
        this.isAdult = isAdult;
    }

    protected void markAsCustom(User user) {
        this.isCustom = true;
        this.createdByUser = user;
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
                bookData.getPage(),
                bookData.isAdult()
        );
    }

    public static Book createCustomBook(BookDetailResponse bookData, User user){
        Book book = createBook(bookData);
        book.markAsCustom(user);
        return book;
    }
}
