package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter @ToString
@Builder
public class SearchBookListResponse {
    private int pageNum; //현재 페이지
    private int totalResultCnt; //전체 결과갯수
    private int itemsPerPage; //한페이지에 출력될 상품 수
    @JsonProperty("items")
    private List<BookData> bookList;

    public static SearchBookListResponse fromOriginData(OriginSearchBookList originList){
        return SearchBookListResponse.builder()
                .pageNum(originList.getStartIndex())
                .totalResultCnt(Math.min(originList.getTotalResults(), 200))
                .itemsPerPage(originList.getItemsPerPage())
                .bookList(
                        originList.getItem().stream()
                            .map(book -> BookData.builder()
                                    .title(book.getTitle())
                                    .isbn(book.getIsbn())
                                    .author(book.getAuthor())
                                    .pubDate(LocalDateTimeUtil.StringToLocalDateAndAddTime(book.getPubDate()))
                                    .cover(book.getCover())
                                    .publisher(book.getPublisher())
                                    .build())
                                .collect(Collectors.toList())

                )
                .build();
    }
}
