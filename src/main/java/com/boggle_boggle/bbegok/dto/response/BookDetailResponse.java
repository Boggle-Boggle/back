package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.OriginBookData;
import com.boggle_boggle.bbegok.utils.LocalDateTimeUtil;
import com.boggle_boggle.bbegok.utils.SpecialCharUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class BookDetailResponse {
    private String title;
    private String isbn;
    private String author;
    private LocalDateTime pubDate;
    private String cover;
    private String publisher;
    private String genre;
    private String plot;
    private int page;

    public static BookDetailResponse fromOriginBookData(OriginBookData origin) {
        return BookDetailResponse.builder()
                .title(SpecialCharUtil.convertSpecialChars(origin.getTitle()))
                .isbn(origin.getIsbn())
                .author(SpecialCharUtil.convertSpecialChars(origin.getAuthor()))
                .pubDate(LocalDateTimeUtil.StringToLocalDateAndAddTime(origin.getPubDate()))
                .cover(origin.getCover())
                .publisher(origin.getPublisher())
                .genre(origin.getCategoryName())
                .plot(SpecialCharUtil.convertSpecialChars(origin.getDescription()))
                .page(origin.getSubInfo().getItemPage())
                .build();
    }
}
