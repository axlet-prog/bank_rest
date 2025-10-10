package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.JwtRefreshRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.service.AuthServiceDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-09 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@SecurityRequirements()
public class AuthController {

    private final AuthServiceDefault authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
        @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
        @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> login(
        @RequestBody JwtRefreshRequest refreshRequest
    ) {
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }
}
