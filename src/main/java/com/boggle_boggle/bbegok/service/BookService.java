package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final AladinClient aladinClient;
    private final OpenFeignConfig openFeignConfig;

    public SearchBookListResponse getSearchBookList(String query, int pageNum){
        if(query == null || query.isEmpty() || query.length()>100) throw new GeneralException(Code.BAD_REQUEST);

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
                        "ISBN",
                        "Big",
                        "JS",
                        "20131101"
                ).getItem().get(0)
        );
    }
}
