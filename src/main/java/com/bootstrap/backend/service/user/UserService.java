package com.bootstrap.backend.service.user;

import com.bootstrap.backend.common.exception.CustomException;
import com.bootstrap.backend.common.exception.ErrorCode;
import com.bootstrap.backend.model.user.User;
import com.bootstrap.backend.model.user.dto.UserLoginRequestDTO;
import com.bootstrap.backend.repository.user.UserRepository;
import com.bootstrap.backend.utils.SecurityProcessor;
import com.bootstrap.backend.utils.TokenProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenProcessor tokenProcessor;
    private final SecurityProcessor securityProcessor;

    public String login(UserLoginRequestDTO userLoginRequest) {
        User user =
                userRepository
                        .findByEmail(userLoginRequest.email())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!securityProcessor.matchPassword(user.getPassword(), userLoginRequest.password())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String token = tokenProcessor.generateToken(user);
        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return token;
    }

    public void register(UserLoginRequestDTO userLoginRequest) {
        User user =
                User.builder()
                        .name(userLoginRequest.email().split("@")[0])
                        .email(userLoginRequest.email())
                        .password(securityProcessor.encodePassword(userLoginRequest.password()))
                        .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
