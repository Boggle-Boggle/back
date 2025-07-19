package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.request.CustomBookRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NormalBookRecordRequest;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.testfactory.BookTestFactory;
import com.boggle_boggle.bbegok.testfactory.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingRecordServiceTest {

    @InjectMocks
    private ReadingRecordService readingRecordService;

    @Mock
    private QueryService queryService;

    @Mock
    private BookService bookService;

    @Mock
    private ReadingRecordRepository readingRecordRepository;

    private User user;
    private String userSeqStr;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.create(1L);
        userSeqStr = String.valueOf(user.getUserSeq());
        when(queryService.getUser(any())).thenReturn(user);
    }

    @Test
    void 일반책_저장성공() {
        NormalBookRecordRequest request = new NormalBookRecordRequest();
        request.setIsbn("1234567890");
        request.setReadStatus(ReadStatus.READING);

        Book dummyBook = BookTestFactory.createBook(1L, request.getIsbn());

        when(bookService.saveBookByRequest(any(), any())).thenReturn(dummyBook);
        when(readingRecordRepository.findByUserAndBook(any(), any())).thenReturn(Optional.empty());
        when(readingRecordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        readingRecordService.save(request, userSeqStr);

        ArgumentCaptor<ReadingRecord> captor = ArgumentCaptor.forClass(ReadingRecord.class);
        verify(readingRecordRepository).save(captor.capture());

        ReadingRecord savedRecord = captor.getValue();
        assertThat(savedRecord.getBook()).isEqualTo(dummyBook);
        assertThat(savedRecord.getUser()).isEqualTo(user);
    }

    @Test
    void 중복_일반책_저장실패() {
        NormalBookRecordRequest request = new NormalBookRecordRequest();
        request.setIsbn("1234567890");
        request.setReadStatus(ReadStatus.READING);

        Book existingBook = BookTestFactory.createBook(1L, request.getIsbn());

        when(bookService.saveBookByRequest(any(), any())).thenReturn(existingBook);
        when(readingRecordRepository.findByUserAndBook(any(), any()))
                .thenReturn(Optional.of(mock(ReadingRecord.class)));

        GeneralException ex = assertThrows(
                GeneralException.class,
                () -> readingRecordService.save(request, userSeqStr)
        );

        assertThat(ex.getErrorCode()).isEqualTo(Code.READING_RECORD_ALREADY_EXIST);
    }

    @Test
    void 커스텀책_저장성공() {
        CustomBookRecordRequest request = new CustomBookRecordRequest();
        request.setCustomBook(BookTestFactory.createCustomBookRequest());
        request.setReadStatus(ReadStatus.READING);

        Book customBook = BookTestFactory.createCustomBook(999L, user);

        when(bookService.saveBookByRequest(any(), any())).thenReturn(customBook);
        when(readingRecordRepository.findByUserAndBook(any(), any())).thenReturn(Optional.empty());
        when(readingRecordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        readingRecordService.save(request, userSeqStr);

        ArgumentCaptor<ReadingRecord> captor = ArgumentCaptor.forClass(ReadingRecord.class);
        verify(readingRecordRepository).save(captor.capture());

        ReadingRecord savedRecord = captor.getValue();
        assertThat(savedRecord.getBook()).isEqualTo(customBook);
        assertThat(savedRecord.getUser()).isEqualTo(user);
    }

    @Test
    void 중복_커스텀책_저장실패() {
        CustomBookRecordRequest request = new CustomBookRecordRequest();
        request.setCustomBook(BookTestFactory.createCustomBookRequest());
        request.setReadStatus(ReadStatus.READING);

        Book customBook = BookTestFactory.createCustomBook(999L, user);

        when(bookService.saveBookByRequest(any(), any())).thenReturn(customBook);
        when(readingRecordRepository.findByUserAndBook(any(), any()))
                .thenReturn(Optional.of(mock(ReadingRecord.class)));

        GeneralException ex = assertThrows(
                GeneralException.class,
                () -> readingRecordService.save(request, userSeqStr)
        );

        assertThat(ex.getErrorCode()).isEqualTo(Code.READING_RECORD_ALREADY_EXIST);
    }
}
