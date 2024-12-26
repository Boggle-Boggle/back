package com.boggle_boggle.bbegok.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteListByReadDateDto {
    private ReadDateDto readDate;
    private List<NoteDto> notes;
}
