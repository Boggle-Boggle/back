package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.NoteDto;
import com.boggle_boggle.bbegok.dto.ReadDateAndIdDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class NotesByReadDateResponse {
    private ReadDateAndIdDto readDate;
    private List<NoteDto> notes;
}
