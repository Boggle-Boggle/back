package com.boggle_boggle.bbegok.entity;

import com.boggle_boggle.bbegok.dto.request.CreateCustomBookRequest;
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

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

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

    //필수값만 받음
    private Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public static Book createBook(BookDetailResponse bookData){
        Book book = new Book(bookData.getTitle(),bookData.getAuthor());
        book.applyDetail(bookData.getIsbn(), bookData.getPublisher(), bookData.getCover(), bookData.getPlot(), bookData.getPage());
        book.publishDate = bookData.getPubDate();
        book.genre = bookData.getGenre();
        book.isAdult = bookData.isAdult();
        return book;
    }

    public static Book createCustomBook(CreateCustomBookRequest bookData, User user){
        Book book = new Book(bookData.getTitle(), bookData.getAuthor());
        book.applyDetail(bookData.getIsbn(), bookData.getPublisher(), bookData.getCoverUrl(), bookData.getPlot(), bookData.getPage());
        book.markAsCustom(user);
        return book;
    }

    protected void markAsCustom(User user) {
        this.isCustom = true;
        this.createdByUser = user;
    }

    private void applyDetail(String isbn, String publisher, String coverUrl, String plot, int page) {
        this.isbn = isbn;
        this.publisher = publisher;
        this.coverUrl = coverUrl;
        this.plot = plot;
        this.page = page;
    }

    public void update(CreateCustomBookRequest newBookData) {
        this.title = newBookData.getTitle();
        this.author = newBookData.getAuthor();
        this.coverUrl = newBookData.getCoverUrl();
        this.publisher = newBookData.getPublisher();
        this.isbn = newBookData.getIsbn();
        this.page = newBookData.getPage();
        this.plot = newBookData.getPlot();
    }
}
