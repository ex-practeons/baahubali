package com.example.iam.service.impl;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.RegisterRequest;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.exception.EmailAlreadyExistsException;
import com.example.iam.exception.InvalidCredentialsException;
import com.example.iam.repository.UserRepository;
import com.example.iam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.STUDENT)
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticate(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
