package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReadDateIndexDto {
    private Long readDateId;
    private int readDateIndex;
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;

    public ReadDateIndexDto(ReadDate readDate, int index) {
        this.readDateId = readDate.getReadDateSeq();
        this.startReadDate = readDate.getStartReadDate();
        this.endReadDate = readDate.getEndReadDate();
        this.readDateIndex = index;
    }
}
