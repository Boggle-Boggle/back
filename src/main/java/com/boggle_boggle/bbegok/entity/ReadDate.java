package com.boggle_boggle.bbegok.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString @Getter
public class ReadDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readDateSeq;

    @Column(name = "start_read_date", nullable = false)
    private LocalDateTime startReadDate;

    @Column(name = "end_read_date")
    private LocalDateTime endReadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_record_seq")
    private ReadingRecord readingRecord;

    protected ReadDate(){}

    public ReadDate(ReadingRecord record, LocalDateTime startReadDate, LocalDateTime endReadDate) {
        this.readingRecord = record;
        this.startReadDate = startReadDate;
        this.endReadDate = endReadDate;
    }

    public static ReadDate createReadDate(ReadingRecord record, LocalDateTime startReadDate, LocalDateTime endReadDate){
        return new ReadDate(record, startReadDate, endReadDate);
    }
}
