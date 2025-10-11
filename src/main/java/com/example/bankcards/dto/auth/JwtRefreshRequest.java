package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Запрос на обновление пары токенов")
public record JwtRefreshRequest(
    @NotBlank
    @Schema(description = "Токен обновления (Refresh Token), полученный при аутентификации",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY3MjYxNDgwMH0.oR1d8_p3q-V8v3tq_p3-oR1d8_V8v3tq_p3-oR1d8_o")
    String refreshToken
) {
}
