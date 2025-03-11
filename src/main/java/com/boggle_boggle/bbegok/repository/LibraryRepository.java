package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.RecordLibraryListDto;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.ReadingRecord;
import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
    // 서재 목록 및 해당 서재의 책 갯수 조회
    @Query("SELECT new com.boggle_boggle.bbegok.dto.LibrariesDto(l.librarySeq, l.libraryName, COUNT(rlm))" +
            " FROM Library l LEFT JOIN ReadingRecordLibraryMapping rlm ON l.librarySeq = rlm.library.librarySeq " +
            "WHERE l.user = :user GROUP BY l")
    List<LibrariesDto> findAllByUserWithBookCount(@Param("user") User user);

    //해당 서재가 있는지 조회
    boolean existsByLibraryNameAndUser(String libraryName,User user);

    //유저의 특정 서재 조회
    Optional<Library> findByUserAndLibrarySeq(User user, Long libraryId);

    //레코드에 대한 서재 매핑 true/false정보
    @Query("""
        SELECT new com.boggle_boggle.bbegok.dto.RecordLibraryListDto(
            l.librarySeq,
            l.libraryName,
            CASE WHEN r.readingRecord = :readingRecord THEN true ELSE false END
        )
        FROM Library l
        LEFT JOIN ReadingRecordLibraryMapping r
         ON l.librarySeq = r.library.librarySeq AND (r.readingRecord = :readingRecord)
        WHERE l.user = :user
    """)
    List<RecordLibraryListDto> findRecordLibraryListDtosInfoByUser(ReadingRecord readingRecord, User user);
}
