package com.example.bankcards.service;

import com.example.bankcards.dto.auth.JwtRefreshRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    JwtResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    JwtResponse refresh(JwtRefreshRequest refreshRequest);

}
