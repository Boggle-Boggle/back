package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class RecordData {
    private Double rating;
    private List<ReadDateAndIdDto> readDateList;
    private List<LibraryListDto> libraries;
    private Boolean isBookVisible;

    public static RecordData fromEntity(ReadingRecord readingRecord){
        return RecordData.builder()
                .rating(readingRecord.getRating())
                .isBookVisible(readingRecord.getIsBooksVisible())
                .readDateList(readingRecord.getReadDateList().stream()
                        //.filter(readDate -> !readDate.getStatus().equals(ReadStatus.pending))
                        .map(ReadDateAndIdDto::new).toList())
                .libraries(readingRecord.getMappingList().stream()
                        .map(library -> new LibraryListDto(library.getLibrary().getLibrarySeq(),library.getLibrary().getLibraryName())).toList())
                .build();
    }
}
