package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.dto.ReadDateAndIdDto;
import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdateReadingRecordRequest {
    private JsonNullable<List<ReadDateAndIdDto>> readDateList = JsonNullable.undefined();
    private JsonNullable<List<Long>> libraryIdList = JsonNullable.undefined();
    private JsonNullable<Double> rating = JsonNullable.undefined();
    private JsonNullable<Boolean> isVisible = JsonNullable.undefined();
}
