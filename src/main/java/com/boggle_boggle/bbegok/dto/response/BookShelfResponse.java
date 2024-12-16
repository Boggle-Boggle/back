package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;
@Getter
@ToString
@Builder
public class BookShelfResponse {
    private List<LibraryBook> books;


    public static BookShelfResponse fromDTO(List<LibraryBook> dto) {
        return BookShelfResponse.builder()
                .books(dto)
                .build();
    }

    public static BookShelfResponse fromPage(List<ReadingRecord> booksPage) {
        return BookShelfResponse.builder()
                .books(LibraryBook.fromReadingRecordList(booksPage))
                .build();
    }
}
