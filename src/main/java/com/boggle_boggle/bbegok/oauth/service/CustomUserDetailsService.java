package com.boggle_boggle.bbegok.oauth.service;

import com.boggle_boggle.bbegok.entity.user.User;
import com.boggle_boggle.bbegok.oauth.entity.UserPrincipal;
import com.boggle_boggle.bbegok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserIdAndIsDeleted(userId, false);
        if (user == null) {
            throw new UsernameNotFoundException("Can not find User.");
        }
        return UserPrincipal.create(user);
    }
}
