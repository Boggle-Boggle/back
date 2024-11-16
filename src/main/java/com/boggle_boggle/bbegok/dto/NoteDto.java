package com.boggle_boggle.bbegok.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoteDto {
    private Long noteId;
    private String title;
    private String content;
}
