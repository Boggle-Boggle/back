package com.boggle_boggle.bbegok.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//커스텀 책용 리퀘스트
@Getter
@Setter
@NoArgsConstructor
public class CustomBookRecordRequest extends NewReadingRecordRequest {

    @Valid
    @NotNull
    private CreateCustomBookRequest customBook;
}
