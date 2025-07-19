package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.OriginBookData;
import com.boggle_boggle.bbegok.dto.OriginDetailBook;
import com.boggle_boggle.bbegok.dto.request.CreateCustomBookRequest;
import com.boggle_boggle.bbegok.dto.request.CustomBookRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NewReadingRecordRequest;
import com.boggle_boggle.bbegok.dto.request.NormalBookRecordRequest;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.entity.Book;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.BookRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.repository.UserFavoriteBookRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final AladinService aladinService;
    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final UserFavoriteBookRepository userFavoriteBookRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }


    @Transactional(readOnly = true)
    public SearchBookListResponse getSearchBookList(String query, int pageNum, String userSeq){
        User user = getUser(userSeq);

        SearchBookListResponse response = SearchBookListResponse.fromOriginData(
                aladinService.getOriginSearchBookList(query, pageNum),
                user.isAdult()
        );

        //내책들인지 확인 후 값 수정 - (1)user_seq의 독서기록/관심도서에서 확인하기
        // 1. 검색된 ISBN 목록 확보
        List<String> isbnList = response.getBookList().stream()
                .map(BookData::getIsbn)
                .filter(Objects::nonNull)
                .toList();

        // 2. 내 책 여부를 일괄로 조회
        Set<String> ownedIsbns = new HashSet<>();
        ownedIsbns.addAll(readingRecordRepository.findReadingRecordIsbns(user.getUserSeq(), isbnList));
        ownedIsbns.addAll(userFavoriteBookRepository.findFavoriteIsbns(user.getUserSeq(), isbnList));

        // 3. myBook 필드 포함해서 새로운 BookData 리스트 생성
        List<BookData> filteredBooks = response.getBookList().stream()
                .map(book -> {
                    boolean isMine = ownedIsbns.contains(book.getIsbn());
                    return book.withMyBook(isMine);
                })
                .toList();

        // 4. 새로운 응답 객체 생성 후 반환
        return response.withBookList(filteredBooks);
    }

    @Transactional(readOnly = true)
    public BookDetailResponse getBook(String isbn, String userSeq){
        User user = getUser(userSeq);
        boolean isMyBook = userFavoriteBookRepository.existsByUser_UserSeqAndBook_Isbn(user.getUserSeq(), isbn);

        return BookDetailResponse.fromOriginBookData(
                aladinService.getOriginBookDetail(isbn),
                user.isAdult(),
                isMyBook
        );
    }


    public Book saveBookByRequest(NewReadingRecordRequest request, User user) {
        if (request instanceof NormalBookRecordRequest normalRequest) {
            return saveBook(normalRequest.getIsbn());
        } else if (request instanceof CustomBookRecordRequest customRequest) {
            return saveCustomBook(customRequest.getCustomBook(), user);
        } else {
            throw new GeneralException(Code.BAD_REQUEST, "알 수 없는 책 유형");
        }
    }

    //책 상세정보에 대해 DB에 존재하면 id를, 존재하지 않으면 저장후 리턴
    private Book saveBook(String isbn){
        Optional<Book> optionalBook = bookRepository.findByIsbnAndIsCustomFalse(isbn);
        if(optionalBook.isPresent()) return optionalBook.get();
        else {
            BookDetailResponse newBook = BookDetailResponse.fromOriginBookData(
                    aladinService.getOriginBookDetail(isbn)
            );
            return bookRepository.save(Book.createBook(newBook));
        }
    }

    private Book saveCustomBook(CreateCustomBookRequest request, User user) {
        return bookRepository.save(Book.createCustomBook(request, user));
    }

    @Transactional
    public void updateCustomBook(Long bookSeq, CreateCustomBookRequest request, User user) {
        Book book = bookRepository.findByBookSeqAndCreatedByUser(bookSeq, user)
                .orElseThrow(() -> new GeneralException(Code.BOOK_NOT_FOUND));
        book.update(request);
    }

}
