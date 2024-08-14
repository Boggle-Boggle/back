package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.OriginBookData;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class BookDetailResponse {
    private String title;
    private String isbn;
    private String author;
    private String pubDate;
    private String cover;
    private String publisher;
    private String jenre;
    private String plot;

    public static BookDetailResponse fromOriginBookData(OriginBookData origin) {
        return BookDetailResponse.builder()
                .title(origin.getTitle())
                .isbn(origin.getIsbn13())
                .author(origin.getAuthor())
                .pubDate(origin.getPubDate())
                .cover(origin.getCover())
                .publisher(origin.getPublisher())
                .jenre(origin.getCategoryName())
                .plot(origin.getDescription())
                .build();
    }
}
