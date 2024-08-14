package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final AladinClient aladinClient;
    private final OpenFeignConfig openFeignConfig;

    public SearchBookListResponse getSearchBookList(String query, int pageNum){
        return SearchBookListResponse.fromOriginData(
                aladinClient.searchItems(
                    openFeignConfig.getTtbKey(),
                    query,
                    "Keyword",
                    "Book",
                    pageNum,
                    10,
                    "Accuracy",
                    "Big",
                    "JS",
                    "20131101"
                )
        );
    }

    public BookDetailResponse getBook(String isbn){
        return BookDetailResponse.fromOriginBookData(
                aladinClient.getItem(
                        openFeignConfig.getTtbKey(),
                        isbn,
                        "ISBN13",
                        "Big",
                        "JS",
                        "20131101"
                ).getItem().get(0)
        );
    }
}
