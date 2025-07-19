package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//일반도서용 요청객체
@Getter
@Setter
@NoArgsConstructor
public class NormalBookRecordRequest extends NewReadingRecordRequest {

    @NotBlank
    private String isbn;
}