package com.edtech.iam.service;

import com.edtech.iam.dto.LoginRequest;
import com.edtech.iam.dto.RegisterRequest;
import com.edtech.iam.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    User authenticate(LoginRequest request);
}
