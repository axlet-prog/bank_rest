package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequest(
    @NotNull
    @Schema(description = "Имя пользователя (логин)", example = "ivanov_i")
    String username,

    @NotNull
    @Schema(description = "Пароль пользователя", example = "MySecurePassword123")
    String password
) {
}
