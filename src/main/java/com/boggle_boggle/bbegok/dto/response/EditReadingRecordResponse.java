package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.*;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditReadingRecordResponse {
    private Long readingRecordId;
    private Double rating;
    private List<ReadDateAndIdDto> readDateList;
    private List<RecordLibraryListDto> libraries;
    private Boolean isBookVisible;

    public static EditReadingRecordResponse from(ReadingRecord entity,
                                                 List<RecordLibraryListDto> libraryDtos){
        return EditReadingRecordResponse.builder()
                .readingRecordId(entity.getReadingRecordSeq())
                .rating(entity.getRating())
                .readDateList(entity.getReadDateList().stream()
                        //.filter(readDate -> !readDate.getStatus().equals(ReadStatus.pending))
                        .map(ReadDateAndIdDto::new).toList())
                .libraries(libraryDtos)
                .isBookVisible(entity.getIsBooksVisible())
                .build();
    }

}
