package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "bookType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NormalBookRecordRequest.class, name = "NORMAL"),
        @JsonSubTypes.Type(value = CustomBookRecordRequest.class, name = "CUSTOM")
})
@Getter
@Setter
@NoArgsConstructor
public abstract class NewReadingRecordRequest {
    @NotNull
    private ReadStatus readStatus;
    private Double rating;
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;
    private List<Long> libraryIdList;
    @NotNull
    private Boolean isVisible;
}
