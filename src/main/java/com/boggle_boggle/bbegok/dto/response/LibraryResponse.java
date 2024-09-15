package com.boggle_boggle.bbegok.dto.response;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LibraryResponse {
    private String libraryName;
    private Long bookCount;

    public static LibraryResponse ofLibrariesDto(LibrariesDto dto){
        return new LibraryResponse(dto.getLibrary().getLibraryName(), dto.getBookCount());
    }
}
