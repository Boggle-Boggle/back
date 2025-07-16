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

    public static SearchBookListResponse fromOriginData(OriginSearchBookList originList, boolean adultVerified){
        return SearchBookListResponse.builder()
                .pageNum(originList.getStartIndex())
                .totalResultCnt(Math.min(originList.getTotalResults(), 200))
                .itemsPerPage(originList.getItemsPerPage())
                .bookList(
                        originList.getItem().stream()
                            .map(book -> {
                                BookData.BookDataBuilder builder = BookData.builder()
                                    .title(SpecialCharUtil.convertSpecialChars(book.getTitle()))
                                    .author(SpecialCharUtil.convertSpecialChars(book.getAuthor()))
                                    .adult(book.isAdult());

                                    if (adultVerified || !book.isAdult()) {
                                        // 성인 인증했거나 성인도서가 아닐 때는 전체 정보를 포함
                                        builder.isbn(book.getIsbn())
                                                .pubDate(LocalDateTimeUtil.StringToLocalDateAndAddTime(book.getPubDate()))
                                                .cover(book.getCover())
                                                .publisher(book.getPublisher());
                                    } // 성인 인증 안했는데 성인도서이면 title, author만 제공 (나머지는 null)
                                    return builder.build();
                                }
                            ).collect(Collectors.toList())
                )
                .build();
    }
}
