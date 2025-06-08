package com.bootstrap.backend.controller.user;

import com.bootstrap.backend.common.annotation.Loggable;
import com.bootstrap.backend.model.user.dto.UserLoginRequestDTO;
import com.bootstrap.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Loggable("UserController")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserLoginRequestDTO userLoginRequest) {
        String token = userService.login(userLoginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "jwt=" + token + "; HttpOnly; Path=/; SameSite=Lax");

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserLoginRequestDTO userLoginRequest) {
        userService.register(userLoginRequest);

        return ResponseEntity.ok("User registered successfully");
    }
}
