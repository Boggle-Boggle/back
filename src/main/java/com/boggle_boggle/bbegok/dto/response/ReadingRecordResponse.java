package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.*;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class ReadingRecordResponse {
    private Long readingRecordId;
    private ReadingRecordBookData bookData;
    private RecordData recordData;

    private ReadingRecordResponse(){}

    public static ReadingRecordResponse fromEntity(ReadingRecord entity){
        return ReadingRecordResponse.builder()
                .readingRecordId(entity.getReadingRecordSeq())
                .bookData(ReadingRecordBookData.fromEntity(entity.getBook()))
                .recordData(RecordData.fromEntity(entity))
                .build();
    }
}
