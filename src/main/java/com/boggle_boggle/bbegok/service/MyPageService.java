package com.boggle_boggle.bbegok.service;

import com.boggle_boggle.bbegok.dto.response.MyPageResponse;
import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.enums.ReadStatus;
import com.boggle_boggle.bbegok.exception.Code;
import com.boggle_boggle.bbegok.exception.exception.GeneralException;
import com.boggle_boggle.bbegok.repository.NoteRepository;
import com.boggle_boggle.bbegok.repository.ReadingRecordRepository;
import com.boggle_boggle.bbegok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final NoteRepository noteRepository;

    public User getUser(String userSeq) {
        User user = userRepository.findByUserSeqAndIsDeleted(Long.valueOf(userSeq), false);
        if(user == null) {
            //탈퇴한 적 있는 회원
            if(userRepository.countByUserSeqAndIsDeleted(Long.valueOf(userSeq), true) > 0) throw new GeneralException(Code.USER_ALREADY_WITHDRAWN);
            else throw new GeneralException(Code.USER_NOT_FOUND);
        }
        return user;
    }

    public MyPageResponse getMyPage(String userSeq) {
        User user = getUser(userSeq);
        //총 읽은 권수 - ReadingRecord 갯수
        int totalReadingCnt = readingRecordRepository.findTotalyReadingCnt(user, ReadStatus.COMPLETED);
        //이번달 읽은 권수 - 이번달에 다 읽은 ReadingRecord 갯수(상태 상관없이 둘다?)
        int monthlyReadingCnt = readingRecordRepository.findMonthlyReadingCnt(user, ReadStatus.COMPLETED);
        //작성한 독서노트 - 작성한 모든 독서노트 갯수
        int totalNote = noteRepository.findByReadingRecord_User(user).size();

        return MyPageResponse.createMyPageResponse(user.getUserName(), totalReadingCnt, monthlyReadingCnt, totalNote);
    }
}
