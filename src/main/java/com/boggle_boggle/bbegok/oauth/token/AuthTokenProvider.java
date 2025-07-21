package com.boggle_boggle.bbegok.oauth.token;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.exception.Code;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenProvider {

    private final AppProperties appProperties; //자동주입
    private Key key; //@PostConstruct를 통해 초기화
    private static final String AUTHORITIES_KEY = "role";

    @PostConstruct
    public void init() {
        String secret = appProperties.getAuth().getTokenSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthToken createAuthToken(com.boggle_boggle.bbegok.entity.user.User user, Date expiry) {
        String userSeq = user.getUserSeq().toString();
        String role = user.getRoleType().getCode();
        return new AuthToken(userSeq, role, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public Authentication getAuthentication(AuthToken authToken) {
        if(authToken.validate()) {
            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else throw new BadCredentialsException(Code.INVALID_ACCESS_TOKEN.getMessage());
    }

}
