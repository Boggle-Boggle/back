package com.boggle_boggle.bbegok.client;

import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.OriginDetailBook;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aladinApi", url = "${aladin.server.url}", configuration = OpenFeignConfig.class)
public interface AladinClient {

    /** 상품 검색 API
     * @param ttbkey ttb키
     * @param query 검색어
     * @param queryType 검색어 종류 : Keyword(title, author), title, author, publisher
     * @param searchTarget 검색 대상 : Book, eBook, Foreign
     * @param start 시작페이지(1 이상)
     * @param maxResults 한페이지당 출력갯수(기본 10, 최대 100)
     * @param sort 정렬순서 : Accuracy, PublishTime, Title
     * @param cover 사진크기 : Big(200px), MidBig(150px)
     * @param output 출력방법 XML, JS(JSON)
     * @param version 최신버전 20131101 필수
     * @return
     */
    @GetMapping("/ttb/api/ItemSearch.aspx")
    OriginSearchBookList searchItems(
            @RequestParam("ttbkey") String ttbkey,
            @RequestParam("Query") String query,
            @RequestParam("QueryType") String queryType,
            @RequestParam("SearchTarget") String searchTarget,
            @RequestParam("Start") int start,
            @RequestParam("MaxResults") int maxResults,
            @RequestParam("Sort") String sort,
            @RequestParam("Cover") String cover,
            @RequestParam("Output") String output,
            @RequestParam("Version") String version
    );

    @GetMapping("/ttb/api/ItemLookUp.aspx")
    OriginDetailBook getItem(
            @RequestParam("ttbkey") String ttbkey,
            @RequestParam("ItemId") String itemId,
            @RequestParam("ItemIdType") String itemIdType, //ISBN13을 권장하나 세트의 경우 ISBN13이 존재X여서 그냥 ISBN사용
            @RequestParam("Cover") String cover,
            @RequestParam("Output") String output,
            @RequestParam("Version") String version
    );
}