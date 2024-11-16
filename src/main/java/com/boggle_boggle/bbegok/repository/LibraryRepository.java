package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.dto.LibrariesDto;
import com.boggle_boggle.bbegok.dto.LibraryDto;
import com.boggle_boggle.bbegok.entity.Library;
import com.boggle_boggle.bbegok.entity.user.User;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    // 서재 목록 및 해당 서재의 책 갯수 조회
    @Query("SELECT new com.boggle_boggle.bbegok.dto.Libraries" +
            "Dto(l, COUNT(rlm))" +
            " FROM Library l LEFT JOIN ReadingRecordLibraryMapping rlm ON l.librarySeq = rlm.library.librarySeq " +
            "WHERE l.user = :user GROUP BY l")
    List<LibrariesDto> findAllByUserWithBookCount(@Param("user") User user);

    //해당 서재가 있는지 조회
    boolean existsByLibraryNameAndUser(String libraryName,User user);

    //유저의 특정 서재 조회
    Optional<Library> findByUserAndLibrarySeq(User user, Long libraryId);

}
