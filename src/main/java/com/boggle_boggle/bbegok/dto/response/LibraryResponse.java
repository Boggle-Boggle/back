package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.RecordByStatusDto;
import com.boggle_boggle.bbegok.enums.LibraryByStatus;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class LibraryResponse {
    private List<LibrariesDto> libraryList;
    private List<RecordByStatusDto> statusList;

    public static LibraryResponse ofDtos(List<LibrariesDto> librariesDtos, List<RecordByStatusDto> readingRecords) {
        LibraryResponse resp = new LibraryResponse();
        resp.libraryList = librariesDtos;
        resp.statusList = new ArrayList<>();
        Long cnt = 0L;
        boolean pendingFlag=false, readingFlag=false, completedFlag=false;
        for(RecordByStatusDto dto : readingRecords) {
            cnt+=dto.getBookCount();
            if(dto.getStatus() == LibraryByStatus.completed) completedFlag = true;
            else if(dto.getStatus() == LibraryByStatus.pending) pendingFlag = true;
            else if(dto.getStatus() == LibraryByStatus.reading) readingFlag = true;
            resp.getStatusList().add(dto);
        }
        if(!pendingFlag) resp.getStatusList().add(new RecordByStatusDto(LibraryByStatus.pending, 0L));
        if(!readingFlag) resp.getStatusList().add(new RecordByStatusDto(LibraryByStatus.reading, 0L));
        if(!completedFlag) resp.getStatusList().add(new RecordByStatusDto(LibraryByStatus.completed, 0L));

        resp.getStatusList().add(0, new RecordByStatusDto(LibraryByStatus.all, cnt));
        return resp;
    }
}
