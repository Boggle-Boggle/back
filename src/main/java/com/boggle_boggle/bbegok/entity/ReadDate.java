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

    @Column(name = "start_read_date")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadDate readDate = (ReadDate) o;
        return readDateSeq != null && readDateSeq.equals(readDate.readDateSeq);
    }

    @Override
    public int hashCode() {
        return readDateSeq != null ? readDateSeq.hashCode() : 0;
    }
}
