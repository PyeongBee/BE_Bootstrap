package com.bootstrap.backend.config;

import com.bootstrap.backend.common.filter.AuthenticationFilter;
import com.bootstrap.backend.repository.user.UserRepository;
import com.bootstrap.backend.utils.TokenProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final TokenProcessor tokenProcessor;

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(tokenProcessor, userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable())
                .cors((cors) -> cors.disable())
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .authorizeHttpRequests(
                        (auth) ->
                                auth.requestMatchers("/", "/login", "/register", "/logout")
                                        .permitAll()
                                        .requestMatchers(
                                                "/swagger-ui.html",
                                                "/api/**",
                                                "/v1/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-resources/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated());

        // JWT 쿠키 인증 필터 추가
        http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 세션 stateless 설정
        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
