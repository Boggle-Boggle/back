package com.boggle_boggle.bbegok.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class OriginSearchBookList {
    private String version;

    private String title;

    private String link;

    private String pubDate;

    private String imageUrl;

    private int totalResults;

    private int startIndex;

    private int itemsPerPage;

    private String query;

    private int searchCategoryId;

    private String searchCategoryName;

    private List<OriginBookData> item;
}