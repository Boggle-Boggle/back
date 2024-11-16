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
    // fromPage 메소드 추가
    public static LibraryBookListResponse fromPage(Page<LibraryBook> page) {
        return LibraryBookListResponse.builder()
                .pageNum(page.getNumber() + 1)
                .totalResultCnt((int) page.getTotalElements())
                .cntPerPage(page.getSize())
                .books(page.getContent())
                .build();
    }

}
