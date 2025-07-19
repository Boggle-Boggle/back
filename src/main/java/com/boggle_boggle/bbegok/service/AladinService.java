package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.OriginBookData;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AladinService {
    private final AladinClient aladinClient;
    private final OpenFeignConfig openFeignConfig;

    //외부 client로부터 책의 상세정보 로드
    public OriginBookData getOriginBookDetail(String isbn){
        OriginBookData bookDeatil = aladinClient.getItem(
                openFeignConfig.getTtbKey(),
                isbn,
                "ISBN",
                "Big",
                "JS",
                "20131101"
        ).getItem().get(0);
        if(bookDeatil == null) throw new GeneralException(Code.BOOK_NOT_FOUND);
        return bookDeatil;
    }

    //외부 client로부터 책의 검색결과 리스트 로드
    public OriginSearchBookList getOriginSearchBookList(String query, int pageNum){
        return aladinClient.searchItems(
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
        );
    }
}
