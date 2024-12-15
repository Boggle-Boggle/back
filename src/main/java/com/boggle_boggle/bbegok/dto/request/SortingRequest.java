package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.enums.SortingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SortingRequest {
    @NotNull
    private SortingType sortingType;
}
