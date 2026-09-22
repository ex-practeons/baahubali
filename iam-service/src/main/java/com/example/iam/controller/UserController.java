package com.example.iam.controller;

import com.example.iam.dto.UserResponse;
import com.example.iam.entity.User;
import com.example.iam.exception.InvalidCredentialsException;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(InvalidCredentialsException::new);

        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
