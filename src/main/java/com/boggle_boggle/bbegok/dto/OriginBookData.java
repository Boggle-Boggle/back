package com.boggle_boggle.bbegok.dto;

import lombok.*;

@Getter @ToString
@NoArgsConstructor
public class OriginBookData {
    private String title;
    private String link;
    private String author;
    private String pubDate;
    private String description;
    private String isbn;
    private String isbn13;
    private Long itemId;
    private Long priceSales;
    private Long priceStandard;
    private String stockStatus;
    private Double mileage;
    private String cover;
    private Long categoryId;
    private String categoryName;
    private String publisher;
    private int customerReviewRank;
    private SubInfo subInfo;
    private boolean adult;
}
