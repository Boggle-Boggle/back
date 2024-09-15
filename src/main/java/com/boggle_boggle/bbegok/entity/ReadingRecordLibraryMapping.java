package com.boggle_boggle.bbegok.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
public class ReadingRecordLibraryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_record_seq")
    private ReadingRecord readingRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_seq")
    private Library library;

    protected ReadingRecordLibraryMapping(){}

    public ReadingRecordLibraryMapping(ReadingRecord readingRecord, Library library) {
        this.readingRecord = readingRecord;
        this.library = library;
    }

    public static ReadingRecordLibraryMapping createReadingRecordLibraryMapping(ReadingRecord readingRecord, Library library) {
        ReadingRecordLibraryMapping mapping = new ReadingRecordLibraryMapping(readingRecord, library);
        return mapping;
    }
}
