package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.RecordByStatusDto;
import com.boggle_boggle.bbegok.enums.LibraryByStatus;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LibraryResponse {
    private List<LibrariesDto> libraryList;
    private List<RecordByStatusDto> statusList;

    public static LibraryResponse ofDtos(List<LibrariesDto> librariesDtos, List<RecordByStatusDto> readingRecords) {
        LibraryResponse resp = new LibraryResponse(librariesDtos, readingRecords);

        Long cnt = 0L;
        for(RecordByStatusDto dto : resp.getStatusList()) cnt+=dto.getBookCount();
        resp.getStatusList().add(new RecordByStatusDto(LibraryByStatus.all, cnt));
        return resp;
    }
}
