package com.boggle_boggle.bbegok.repository.user;

import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserIdAndIsDeleted(String userId, boolean isDeleted);

    Optional<User> findByUserName(String nickname);

    long countByUserIdAndIsDeleted(String userId, boolean b);
}
