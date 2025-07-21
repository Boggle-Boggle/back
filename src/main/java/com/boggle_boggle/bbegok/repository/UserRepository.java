package com.boggle_boggle.bbegok.repository;

import com.boggle_boggle.bbegok.entity.user.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserIdAndIsDeleted(String userId, @NotNull boolean isDeleted);

    Optional<User> findByUserName(String nickname);

    User findByUserSeqAndIsDeleted(Long userSeq, @NotNull boolean b);

    long countByUserSeqAndIsDeleted(Long userSeq, @NotNull Boolean isDeleted);

    boolean existsByUserName(String nickname);

    boolean existsByUserIdAndIsDeletedFalse(String oauth2Id);
}
