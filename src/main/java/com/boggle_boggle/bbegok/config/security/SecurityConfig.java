package com.boggle_boggle.bbegok.config.security;

import com.boggle_boggle.bbegok.config.properties.AppProperties;
import com.boggle_boggle.bbegok.config.properties.CorsProperties;
import com.boggle_boggle.bbegok.oauth.entity.RoleType;
import com.boggle_boggle.bbegok.oauth.exception.RestAuthenticationEntryPoint;
import com.boggle_boggle.bbegok.oauth.filter.TokenAuthenticationFilter;
import com.boggle_boggle.bbegok.oauth.handler.CustomAccessDeniedHandler;
import com.boggle_boggle.bbegok.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.boggle_boggle.bbegok.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.boggle_boggle.bbegok.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.boggle_boggle.bbegok.oauth.service.CustomOAuth2UserService;
import com.boggle_boggle.bbegok.oauth.service.CustomUserDetailsService;
import com.boggle_boggle.bbegok.oauth.token.AuthTokenProvider;
import com.boggle_boggle.bbegok.repository.redis.TermsRepository;
import com.boggle_boggle.bbegok.repository.user.UserRefreshTokenRepository;
import com.boggle_boggle.bbegok.repository.user.UserRepository;
import com.boggle_boggle.bbegok.service.AccessTokenService;
import com.boggle_boggle.bbegok.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    //private final TokenAccessDeniedHandler tokenAccessDeniedHandler; //CustomAccessDeniedHandler로 통합됨
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final AccessTokenService accessTokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint()) //인증되지 않은 요청이 보호된 리소스에 접근할때
                        .accessDeniedHandler(customAccessDeniedHandler)) //인증된 사용자가 접근권한이 없을때

                //url 접근권한처리
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        //인증 관련 API는 모든 요청이 허용
                        .requestMatchers("/auth/**").permitAll()
                        
                        //애플로그인 관련 요청 허용
                        .requestMatchers("/oauth2/apple/**").permitAll()
                        .requestMatchers("/login/oauth2/code/apple/**").permitAll()

                        //guest : 약관동의, 닉네임 수정 API에만 접근 가능
                        .requestMatchers("/user/**").hasAnyAuthority(RoleType.GUEST.getCode(),RoleType.USER.getCode(),RoleType.LIMITED_USER.getCode())
                        //임시로 게스트에게 허용
                        .requestMatchers("/books/**").hasAnyAuthority(RoleType.USER.getCode())
                        .requestMatchers("/libraries/**").hasAnyAuthority(RoleType.USER.getCode())
                        .requestMatchers("/recent-searches/**").hasAnyAuthority(RoleType.USER.getCode())
                        .requestMatchers("/reading-record/**").hasAnyAuthority(RoleType.GUEST.getCode(),RoleType.USER.getCode())
                        .requestMatchers("/library/**").hasAnyAuthority(RoleType.USER.getCode())
                        .requestMatchers("/bookshelf/**").hasAnyAuthority(RoleType.USER.getCode())
                        .requestMatchers("/mypage/**").hasAnyAuthority(RoleType.USER.getCode())
                        .anyRequest().hasAuthority(RoleType.USER.getCode()))

                //토큰 검증 필터
                .addFilterBefore( new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)

                //OAuth2 로그인 요청 처리
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization") //이 엔드포인트 요청에 대해 아래에 쿠키로 요청데이터저장
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/*/oauth2/code/*")) //각 인증페이지로 리디렉션된 요청을 처리, 액세스토큰 교환
                        .userInfoEndpoint(userInfo -> userInfo //사용자 정보 획득
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .failureHandler(oAuth2AuthenticationFailureHandler()));

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 토큰 필터 설정
     * */
//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter(tokenProvider);
//    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                accessTokenService,
                tokenProvider,
                appProperties,
                userRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userRepository
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors 설정
     * */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(corsProperties.getAllowCredentials());
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }
}
