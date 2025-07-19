package com.boggle_boggle.bbegok.testfactory;

import com.boggle_boggle.bbegok.dto.request.CreateCustomBookRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class BookTestFactory {

    public static Book createBook(Long bookSeq, String isbn) {
        BookDetailResponse dummyResponse = BookDetailResponse.builder()
                .myBook(false)
                .title("테스트 책")
                .isbn(isbn)
                .author("테스트 작가")
                .pubDate(LocalDateTime.now())
                .cover("https://example.com/cover.jpg")
                .publisher("테스트 출판사")
                .genre("테스트 장르")
                .plot("테스트용 책입니다.")
                .page(123)
                .link("https://example.com/book")
                .adult(false)
                .build();

        Book book = Book.createBook(dummyResponse);
        ReflectionTestUtils.setField(book, "bookSeq", bookSeq);
        return book;
    }

    public static Book createCustomBook(Long bookSeq, User user) {
        CreateCustomBookRequest dummyResponse = createCustomBookRequest();

        Book book = Book.createCustomBook(dummyResponse, user);
        ReflectionTestUtils.setField(book, "bookSeq", bookSeq);
        return book;
    }

    public static CreateCustomBookRequest createCustomBookRequest() {
        return CreateCustomBookRequest.builder()
                .title("소년이 올까말까")
                .author("금강")
                .coverUrl("https://example.com")
                .publisher(null)
                .isbn("987654321")
                .page(100)
                .plot("올랑말랑갈랑말랑")
                .build();
    }
}
