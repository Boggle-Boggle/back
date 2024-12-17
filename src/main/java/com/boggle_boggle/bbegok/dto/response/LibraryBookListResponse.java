package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
@Builder
public class LibraryBookListResponse {
    private int pageNum; //현재 페이지
    private int totalResultCnt; //전체 결과갯수
    private int itemsPerPage; //한페이지에 출력될 상품 수
    @JsonProperty("items")
    private List<LibraryBook> books;

    // fromPage 메서드를 추가하여 Page<Book>를 LibraryBookListResponse로 변환
    // fromPage 메소드 추가
    public static LibraryBookListResponse fromPage(Page<ReadingRecord> page) {
        return LibraryBookListResponse.builder()
                .pageNum(page.getNumber() + 1)
                .totalResultCnt((int) page.getTotalElements())
                .itemsPerPage(page.getSize())
                .books(LibraryBook.fromReadingRecordList(page.getContent()))
                .build();
    }

}
