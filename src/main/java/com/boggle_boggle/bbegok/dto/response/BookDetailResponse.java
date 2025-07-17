package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.OriginBookData;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import com.boggle_boggle.bbegok.utils.SpecialCharUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class BookDetailResponse {
    @JsonProperty("isMyBook")
    private boolean myBook;
    private String title;
    private String isbn;
    private String author;
    private LocalDateTime pubDate;
    private String cover;
    private String publisher;
    private String genre;
    private String plot;
    private int page;
    private String link;
    private boolean adult;

    public static BookDetailResponse fromOriginBookData(OriginBookData origin, boolean adultVerified, boolean isMyBook) {
        BookDetailResponse.BookDetailResponseBuilder builder = BookDetailResponse.builder()
                .title(SpecialCharUtil.convertSpecialChars(origin.getTitle()))
                .author(SpecialCharUtil.convertSpecialChars(origin.getAuthor()))
                .isbn(origin.getIsbn())
                .pubDate(LocalDateTimeUtil.StringToLocalDateAndAddTime(origin.getPubDate()))
                .publisher(origin.getPublisher())
                .genre(origin.getCategoryName())
                .page(origin.getSubInfo().getItemPage())
                .link(origin.getLink())
                .myBook(isMyBook)
                .adult(origin.isAdult());

        // 성인이거나 성인도서가 아닐때만 커버+줄거리 추가
        if (adultVerified || !origin.isAdult()) {
            builder.cover(origin.getCover())
                    .plot(SpecialCharUtil.convertSpecialChars(origin.getDescription()))
                    .build();
        }
        return builder.build();
    }
}
