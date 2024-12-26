package com.boggle_boggle.bbegok.controller;

import com.boggle_boggle.bbegok.dto.NoteDto;
import com.boggle_boggle.bbegok.dto.ReadDateDto;
import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import com.boggle_boggle.bbegok.dto.request.NewNoteRequest;
import com.boggle_boggle.bbegok.dto.response.NotesByReadDateResponse;
import com.boggle_boggle.bbegok.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-record")
public class NoteController {

    private final NoteService noteService;

    //독서노트 조회
    @GetMapping("/{recordId}/note")
    public DataResponseDto<List<NotesByReadDateResponse>> getNote(@PathVariable(name = "recordId") Long recordId,
                                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return DataResponseDto.of(noteService.getNote(recordId, userDetails.getUsername()));
    }


    //새로운 독서노트 등록
    @PostMapping("/{recordId}/note")
    public DataResponseDto<Void> saveNote(@PathVariable(name = "recordId") Long recordId,
                                            @RequestBody NewNoteRequest request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        noteService.saveNote(recordId, request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서노트 수정
    @PatchMapping("/{recordId}/note/{noteId}")
    public DataResponseDto<Void> updateNote(@PathVariable(name = "recordId") Long recordId,
                                            @PathVariable(name = "noteId") Long noteId,
                                             @RequestBody NewNoteRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        noteService.updateNote(recordId, noteId, request, userDetails.getUsername());
        return DataResponseDto.empty();
    }

    //독서노트 삭제
    @DeleteMapping("/{recordId}/note/{noteId}")
    public DataResponseDto<Void> deleteNote(@PathVariable(name = "recordId") Long recordId,
                                            @PathVariable(name="noteId") Long noteId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        noteService.deleteNote(recordId, noteId, userDetails.getUsername());
        return DataResponseDto.empty();
    }
}
