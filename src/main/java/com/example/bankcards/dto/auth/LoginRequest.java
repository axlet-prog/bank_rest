package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequest(
    @NotNull
    @Schema(description = "Имя пользователя (логин)", example = "user")
    String username,

    @NotNull
    @Schema(description = "Пароль пользователя", example = "1234")
    String password
) {
}
