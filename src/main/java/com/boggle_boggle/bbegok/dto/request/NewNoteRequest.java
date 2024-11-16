package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewNoteRequest {
    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String content;
}
