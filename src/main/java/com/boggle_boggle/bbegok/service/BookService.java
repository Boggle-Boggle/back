package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.client.AladinClient;
import com.boggle_boggle.bbegok.config.openfeign.OpenFeignConfig;
import com.boggle_boggle.bbegok.dto.OriginSearchBookList;
import com.boggle_boggle.bbegok.dto.response.BookDetailResponse;
import com.boggle_boggle.bbegok.dto.response.SearchBookListResponse;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final AladinClient aladinClient;
    private final OpenFeignConfig openFeignConfig;
    private final UserRepository userRepository;

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

        return SearchBookListResponse.fromOriginData(
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
    }

    public BookDetailResponse getBook(String isbn){
        return BookDetailResponse.fromOriginBookData(
                aladinClient.getItem(
                        openFeignConfig.getTtbKey(),
                        isbn,
                        "ISBN",
                        "Big",
                        "JS",
                        "20131101"
                ).getItem().get(0)
        );
    }
}
