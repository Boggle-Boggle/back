package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.BookShelfItem;
import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.dto.ReadingRecordAndDateDTO;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class BookShelfResponse {
    private List<BookShelfItem> books;


    public static BookShelfResponse fromDTO(List<BookShelfItem> dto) {
        return BookShelfResponse.builder()
                .books(dto)
                .build();
    }

    public static BookShelfResponse fromPage(List<ReadingRecordAndDateDTO> booksPage) {
        return BookShelfResponse.builder()
                .books(booksPage.stream().map(bp -> BookShelfItem.builder()
                        .readingRecordId(bp.getReadingRecord().getReadingRecordSeq())
                        .title(bp.getReadingRecord().getBook().getTitle())
                        .page(bp.getReadingRecord().getBook().getPage())
                        .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
