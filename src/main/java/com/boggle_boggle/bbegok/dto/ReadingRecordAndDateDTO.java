package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReadingRecordAndDateDTO {
    private ReadingRecord readingRecord;
    private ReadDate readDate;
}
