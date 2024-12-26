package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.entity.ReadDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadDateAndIdDto {
    private Long readDateId;
    private LocalDateTime startReadDate;
    private LocalDateTime endReadDate;

    public ReadDateAndIdDto(ReadDate readDate) {
        this.readDateId = readDate.getReadDateSeq();
        this.startReadDate = readDate.getStartReadDate();
        this.endReadDate = readDate.getEndReadDate();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadDateAndIdDto that = (ReadDateAndIdDto) o;
        return Objects.equals(readDateId, that.readDateId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(readDateId);
    }
}
