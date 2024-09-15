package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.ReadingRecordResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadDate;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.BookRepository;
import com.boggle_boggle.bbegok.repository.LibraryRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingRecordService {
    private final ReadingRecordRepository readingRecordRepository;
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void saveReadingRecord(NewReadingRecordRequest request, String userId) {
        //이미 해당 isbn이 저장되어있는지 확인 -> 없다면 새로 저장
        Book book = bookRepository.findByIsbn(request.getIsbn());
        if(book == null) {
            BookDetailResponse newBookData = bookService.getBook(request.getIsbn());
            if(newBookData == null) throw new GeneralException(Code.BOOK_NOT_FOUND);
            book = bookRepository.save(Book.createBook(newBookData));
        }

        //독서기록 저장 > 다대다(Library - mapping - readingRecord) 매핑 저장
        User user = getUser(userId);

        List<Library> libraries = new ArrayList<>();
        for (String libraryName : request.getLibraryNameList()) {
            Library library = libraryRepository.findByUserAndLibraryName(user, libraryName);
            if (library == null) throw new GeneralException(Code.LIBRARY_NOT_FOUND);
            libraries.add(library);
        }

        ReadingRecord readingRecord = ReadingRecord.createReadingRecord(
                user,
                book,
                request.getStartReadDate(),
                request.getEndReadDate(),
                libraries,
                request.getRating(),
                request.isVisible(),
                request.getReadStatus()
        );

        readingRecordRepository.save(readingRecord);
    }

    public ReadingRecordResponse getReadingRecord(String isbn, String userId) {
        Book book = bookRepository.findByIsbn(isbn);
        User user = getUser(userId);
        return ReadingRecordResponse.fromReadingRecord(readingRecordRepository.findByUserAndBook(user, book));
    }
}
