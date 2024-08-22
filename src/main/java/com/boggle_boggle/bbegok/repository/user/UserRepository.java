package com.boggle_boggle.bbegok.repository.user;

import com.boggle_boggle.bbegok.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);

    Optional<User> findByUserName(String nickname);

}
