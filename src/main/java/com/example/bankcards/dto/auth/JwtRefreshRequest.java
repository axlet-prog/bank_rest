package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Запрос на обновление пары токенов")
public record JwtRefreshRequest(
    @NotBlank
    @Schema(description = "Токен обновления (Refresh Token), полученный при аутентификации",
            example = "51aaadb8-fd23-403a-bfd5-b6adad889d2d")
    String refreshToken
) {
}
