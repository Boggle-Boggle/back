package com.boggle_boggle.bbegok.dto;

import com.boggle_boggle.bbegok.dto.response.LibraryResponse;
import com.boggle_boggle.bbegok.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LibrariesDto {
    private Long libraryId;
    private String libraryName;
    private Long bookCount;
}
