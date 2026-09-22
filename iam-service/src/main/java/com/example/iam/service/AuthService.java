package com.example.iam.service;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.RegisterRequest;
import com.example.iam.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    User authenticate(LoginRequest request);
}
