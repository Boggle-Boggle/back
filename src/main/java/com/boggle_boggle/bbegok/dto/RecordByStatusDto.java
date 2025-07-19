package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.enums.LibraryByStatus;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordByStatusDto {
    private LibraryByStatus status;
    private String libraryName;
    private Long bookCount;


    public RecordByStatusDto(ReadStatus readStatus, Long bookCount){
        LibraryByStatus libraryByStatus;
        if(readStatus == ReadStatus.COMPLETED) libraryByStatus = LibraryByStatus.completed;
        //else if(readStatus == ReadStatus.pending) libraryByStatus = LibraryByStatus.pending;
        else if(readStatus == ReadStatus.READING) libraryByStatus = LibraryByStatus.reading;
        else libraryByStatus = LibraryByStatus.all;

        this.status = libraryByStatus;
        this.libraryName = libraryByStatus.getLibraryName();
        this.bookCount = bookCount;
    }

    public RecordByStatusDto(Long bookCount) {
        this.status = LibraryByStatus.all;
        this.libraryName = status.getLibraryName();
        this.bookCount = bookCount;
    }

    public RecordByStatusDto(LibraryByStatus libraryByStatus, Long bookCount) {
        this.status = libraryByStatus;
        this.libraryName = libraryByStatus.getLibraryName();
        this.bookCount = bookCount;
    }
}
