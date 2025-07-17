package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.BookData;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.UserFavoriteBook;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
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
    private final AladinClient aladinClient;
    private final OpenFeignConfig openFeignConfig;
    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final UserFavoriteBookRepository userFavoriteBookRepository;

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    public SearchBookListResponse getSearchBookList(String query, int pageNum, String userSeq){
        if(query == null || query.isEmpty() || query.length()>100) throw new GeneralException(Code.BAD_REQUEST);
        User user = getUser(userSeq);

        SearchBookListResponse response = SearchBookListResponse.fromOriginData(
                aladinClient.searchItems(
                        openFeignConfig.getTtbKey(),
                        query,
                        "Keyword",
                        "Book",
                        pageNum,
                        10,
                        "Accuracy",
                        "Big",
                        "JS",
                        "20131101"
                ),
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

    public BookDetailResponse getBook(String isbn, String userSeq){
        User user = getUser(userSeq);

        //==내 책들에 포함된건지 확인
        boolean isMyBook = readingRecordRepository.existsByUser_UserSeqAndBook_Isbn(user.getUserSeq(), isbn)
                || userFavoriteBookRepository.existsByUser_UserSeqAndBook_Isbn(user.getUserSeq(), isbn);

        return BookDetailResponse.fromOriginBookData(
                aladinClient.getItem(
                        openFeignConfig.getTtbKey(),
                        isbn,
                        "ISBN",
                        "Big",
                        "JS",
                        "20131101"
                ).getItem().get(0),
                user.isAdult(),
                isMyBook
        );
    }
}
