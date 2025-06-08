package com.bootstrap.backend.common.filter;

import com.bootstrap.backend.common.exception.CustomException;
import com.bootstrap.backend.common.exception.ErrorCode;
import com.bootstrap.backend.model.user.User;
import com.bootstrap.backend.repository.user.UserRepository;
import com.bootstrap.backend.utils.TokenProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final TokenProcessor tokenProcessor;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String email = tokenProcessor.extractEmail(cookie.getValue());
                    User user =
                            userRepository
                                    .findByEmail(email)
                                    .orElseThrow(
                                            () -> new CustomException(ErrorCode.USER_NOT_FOUND));

                    if (!tokenProcessor.validateToken(cookie.getValue(), user)) {
                        throw new CustomException(ErrorCode.INVALID_TOKEN);
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, user.getPassword());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
