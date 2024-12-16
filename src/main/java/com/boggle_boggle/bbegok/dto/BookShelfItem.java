package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.ReadingRecord;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class BookShelfItem {
    private Long readingRecordId;
    private String title;
    private int page;
}
