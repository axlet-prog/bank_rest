package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegisterRequest(
    @NotNull
    @Schema(description = "Имя пользователя (логин) для нового аккаунта", example = "petrov_p")
    String username,

    @NotNull
    @Schema(description = "Пароль для нового аккаунта", example = "NewPassword456!")
    String password
) {
}
