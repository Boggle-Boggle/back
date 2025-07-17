package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import com.boggle_boggle.bbegok.utils.SpecialCharUtil;
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

    public SearchBookListResponse withBookList(List<BookData> newBookList) {
        return SearchBookListResponse.builder()
                .pageNum(this.pageNum)
                .totalResultCnt(this.totalResultCnt)
                .itemsPerPage(this.itemsPerPage)
                .bookList(newBookList)
                .build();
    }

    public static SearchBookListResponse fromOriginData(OriginSearchBookList originList, boolean adultVerified){
        return SearchBookListResponse.builder()
                .pageNum(originList.getStartIndex())
                .totalResultCnt(Math.min(originList.getTotalResults(), 200))
                .itemsPerPage(originList.getItemsPerPage())
                .bookList(
                        originList.getItem().stream()
                            .map(book -> {
                                BookData.BookDataBuilder builder = BookData.builder()
                                    .isbn(book.getIsbn())
                                    .title(SpecialCharUtil.convertSpecialChars(book.getTitle()))
                                    .author(SpecialCharUtil.convertSpecialChars(book.getAuthor()))
                                    .adult(book.isAdult())
                                    .publisher(book.getPublisher());;

                                    // 성인이거나 성인도서가 아닐때만 커버 추가
                                    if (adultVerified || !book.isAdult()) builder.cover(book.getCover());
                                    return builder.build();
                                }
                            ).collect(Collectors.toList())
                )
                .build();
    }
}
