package com.boggle_boggle.bbegok.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordLibraryListDto {
    private Long libraryId;
    private String libraryName;
    private boolean isSelected;
}
