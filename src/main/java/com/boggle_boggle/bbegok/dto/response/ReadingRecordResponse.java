package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.NoteDto;
import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadingRecordResponse {
    private String cover;
    private String title;
    private String author;
    private String publisher;
    private LocalDateTime pubDate;
    private Double rating;
    private ReadStatus status;
    private List<ReadDateDto> readDateList;
    private List<String> libraries;
    private List<NoteDto> notes;

    private ReadingRecordResponse(){}

    public static ReadingRecordResponse fromReadingRecord(ReadingRecord entity){
        ReadingRecordResponse response = new ReadingRecordResponse();
        response.cover = entity.getBook().getImageUrl();
        response.title = entity.getBook().getTitle();
        response.author = entity.getBook().getAuthor();
        response.publisher = entity.getBook().getPublisher();
        response.pubDate = entity.getBook().getPublishDate();
        response.rating = entity.getRating();
        response.status = entity.getStatus();
        response.readDateList = entity.getReadDateList().stream()
                .map(readDate -> new ReadDateDto(readDate.getStartReadDate(), readDate.getEndReadDate())).toList();
        response.libraries = entity.getMappingList().stream()
                .map(mapping -> mapping.getLibrary().getLibraryName()).toList();
        response.notes = entity.getNoteList().stream()
                .map(note -> new NoteDto(note.getTitle(), note.getContent())).toList();
        return response;
    }
}
