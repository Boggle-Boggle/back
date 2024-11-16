package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibraryBook;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
@Getter
@ToString
@Builder
public class BookShelfResponse {
    private List<LibraryBook> books;


    public static BookShelfResponse fromDTO(List<LibraryBook> dto) {
        return BookShelfResponse.builder()
                .books(dto)
                .build();
    }
}
