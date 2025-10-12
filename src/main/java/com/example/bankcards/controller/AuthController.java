package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.JwtRefreshRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Аутентификация и Авторизация", description = "Эндпоинты для входа, регистрации и обновления токенов")
@SecurityRequirements()
public class AuthController {

    private final AuthService authService;

    @Operation(
        description = "Позволяет пользователю войти в систему по имени пользователя и паролю. В случае успеха возвращает пару JWT токенов (access и refresh).")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
        @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(description = "Создает нового пользователя в системе, возвращая пару JWT токенов.")
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
        @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Operation(
        description = "Позволяет получить новую пару JWT токенов (access и refresh) при предоставлении действительного refresh токена.")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
        @RequestBody JwtRefreshRequest refreshRequest
    ) {
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }
}
