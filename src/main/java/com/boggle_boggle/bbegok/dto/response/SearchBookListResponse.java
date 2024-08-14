package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter @ToString
@Builder
public class SearchBookListResponse {
    private int pageNum;
    private List<BookData> bookList;

    public static SearchBookListResponse fromOriginData(OriginSearchBookList originList){
        return SearchBookListResponse.builder()
                .pageNum(originList.getStartIndex())
                .bookList(
                        originList.getItem().stream()
                            .map(book -> BookData.builder()
                                    .title(book.getTitle())
                                    .isbn(book.getIsbn13())
                                    .author(book.getAuthor())
                                    .pubDate(book.getPubDate())
                                    .cover(book.getCover())
                                    .publisher(book.getPublisher())
                                    .build())
                                .collect(Collectors.toList())

                )
                .build();
    }
}
