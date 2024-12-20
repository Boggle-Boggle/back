package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.RecordByStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LibraryResponse {
    private List<LibrariesDto> libraryList;
    private List<RecordByStatusDto> statusList;

    public static LibraryResponse ofDtos(List<LibrariesDto> librariesDtos, List<RecordByStatusDto> readingRecords) {
        return new LibraryResponse(librariesDtos, readingRecords);
    }
}
