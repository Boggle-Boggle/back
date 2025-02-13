package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.enums.ReadStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull @NotBlank
    private String isbn;
    @NotNull
    private ReadStatus readStatus;
    private Double rating;
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;
    private List<Long> libraryIdList;
    private Boolean isVisible;
}
