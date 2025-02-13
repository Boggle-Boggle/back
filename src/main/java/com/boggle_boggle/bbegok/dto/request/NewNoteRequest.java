package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.dto.PagesDto;
import com.boggle_boggle.bbegok.dto.ReadDateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewNoteRequest {

    private JsonNullable<Long> readDateId = JsonNullable.undefined();

    private JsonNullable<LocalDateTime> selectedDate = JsonNullable.undefined();

    @Size(max = 50)
    private JsonNullable<String> title = JsonNullable.undefined();

    @Size(max = 1024)
    private JsonNullable<String> content = JsonNullable.undefined();

    @Size(min = 1, max = 99999)
    private JsonNullable<Integer> page = JsonNullable.undefined();

    private JsonNullable<PagesDto> pages = JsonNullable.undefined();

    private JsonNullable<List<String>> tags = JsonNullable.undefined();
}
