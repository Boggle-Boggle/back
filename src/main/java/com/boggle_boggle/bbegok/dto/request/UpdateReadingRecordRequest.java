package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdateReadingRecordRequest {
    private String isbn;
    private ReadStatus readStatus;
    private Double rating;
    List<ReadDateDto> readDateList;
    private List<Long> libraryIdList;
    private Boolean isVisible;
}
