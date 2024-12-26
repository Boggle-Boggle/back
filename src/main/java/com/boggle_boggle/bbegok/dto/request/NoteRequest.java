package com.boggle_boggle.bbegok.dto.request;

import com.boggle_boggle.bbegok.dto.NoteListByReadDateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteRequest {
    private List<NoteListByReadDateDto> notesByReadDate;
}
