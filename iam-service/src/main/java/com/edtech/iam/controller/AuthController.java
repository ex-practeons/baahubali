package com.edtech.iam.controller;

import com.edtech.iam.dto.AuthResponse;
import com.edtech.iam.dto.LoginRequest;
import com.edtech.iam.dto.RegisterRequest;
import com.edtech.iam.dto.UserResponse;
import com.edtech.iam.entity.User;
import com.edtech.iam.security.AuthCookieFactory;
import com.edtech.iam.security.JwtService;
import com.edtech.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthCookieFactory authCookieFactory;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        ResponseCookie cookie = authCookieFactory.buildAuthCookie(jwtService.generateToken(user));

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse("Registration successful", toUserResponse(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(request);
        ResponseCookie cookie = authCookieFactory.buildAuthCookie(jwtService.generateToken(user));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse("Login successful", toUserResponse(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie expiredCookie = authCookieFactory.buildExpiredAuthCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
