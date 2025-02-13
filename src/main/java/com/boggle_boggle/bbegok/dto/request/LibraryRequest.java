package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LibraryRequest {
    @NotBlank
    @NotNull
    @Size(max = 15)
    private String libraryName;
}
