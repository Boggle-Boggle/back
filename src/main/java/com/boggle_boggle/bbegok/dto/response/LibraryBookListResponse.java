package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class LibraryBookListResponse {
    private int pageNum;
    private int totalResultCnt;
    private int cntPerPage;
    private List<LibraryBook> books;

    // fromPage 메서드를 추가하여 Page<Book>를 LibraryBookListResponse로 변환
    public static LibraryBookListResponse fromPage(Page<Book> booksPage) {
        List<LibraryBook> libraryBooks = booksPage.stream()
                .map(LibraryBook::fromEntity) // 각 Book을 LibraryBook으로 변환
                .collect(Collectors.toList());

        return LibraryBookListResponse.builder()
                .pageNum(booksPage.getNumber())
                .totalResultCnt((int) booksPage.getTotalElements())
                .cntPerPage(booksPage.getSize())
                .books(libraryBooks)
                .build();
    }
}
