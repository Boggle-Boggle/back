package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCustomBookRequest {
    @NotBlank
    @Size(max=255)
    private String title;

    @NotBlank
    @Size(max=255)
    private String author;

    @Size(max=1000)
    private String coverUrl;

    @Size(max=255)
    private String publisher;

    @Size(max=20)
    private String isbn;

    @PositiveOrZero
    private int page;

    @Size(max=1000)
    private String plot;
}
