package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewReadingRecordRequest {
    private String isbn;
    private ReadStatus readStatus;
    private double rating;
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;
    private List<String> libraryNameList;
    private boolean isVisible;
}
