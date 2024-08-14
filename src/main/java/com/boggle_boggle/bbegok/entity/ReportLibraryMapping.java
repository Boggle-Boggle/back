package com.boggle_boggle.bbegok.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
public class ReportLibraryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_seq")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_library_mapping")
    private ReportLibraryMapping reportLibraryMapping;

    protected ReportLibraryMapping(){}

    public static ReportLibraryMapping createReportLibraryMapping(){
        return new ReportLibraryMapping();
    }
}
