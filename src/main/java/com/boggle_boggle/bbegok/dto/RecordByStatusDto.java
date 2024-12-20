package com.boggle_boggle.bbegok.dto;

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
    private ReadStatus status;
    private String libraryName;
    private Long bookCount;

    public RecordByStatusDto(ReadStatus status, Long bookCount){
        this.status = status;
        this.libraryName = status.getLibraryName();
        this.bookCount = bookCount;
    }
}
