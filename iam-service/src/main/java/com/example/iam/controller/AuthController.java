package com.example.iam.controller;

import com.example.iam.dto.ApiResponse;
import com.example.iam.dto.AuthenticationResult;
import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.RegisterRequest;
import com.example.iam.dto.UserResponse;
import com.example.iam.security.AuthCookieFactory;
import com.example.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieFactory authCookieFactory;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResult result = authService.register(request);
        ResponseCookie cookie = authCookieFactory.buildAuthCookie(result.token());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Account registered successfully",
                        UserResponse.from(result.user())
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResult result = authService.authenticate(request);
        ResponseCookie cookie = authCookieFactory.buildAuthCookie(result.token());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Login successful",
                        UserResponse.from(result.user())
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> logout(@RequestHeader("X-User-Id") String userId) {
        authService.logout(userId);
        ResponseCookie expiredCookie = authCookieFactory.buildExpiredAuthCookie();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(ApiResponse.success(HttpStatus.OK.value(), "Logout successful", Map.of()));
    }
}
