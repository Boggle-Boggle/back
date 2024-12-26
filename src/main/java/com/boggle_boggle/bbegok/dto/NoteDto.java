package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.Note;
import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.embed.Pages;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {
    private Long noteId;
    private String title;
    private LocalDateTime selectedDate;
    private Integer page;
    private PagesDto pages;
    private String content;
    private List<String> tags;

    // Note를 NoteDto로 변환하는 메서드
    public static NoteDto fromEntity(Note note) {
        return new NoteDto(
                note.getNoteSeq(),
                note.getTitle(),
                note.getSelectedDate(),
                note.getPage(),
                note.getPages() == null ? null : new PagesDto(note.getPages().getStartPage(), note.getPages().getEndPage()),
                note.getContent(),
                note.getTags()
        );
    }
}
