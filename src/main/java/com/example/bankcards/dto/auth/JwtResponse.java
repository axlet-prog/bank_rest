package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с токенами доступа и обновления")
public record JwtResponse(
    @Schema(description = "Токен доступа (Access Token)",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Miwic3ViIjoidXNlciIsImlhdCI6MTc2MDExMzI4MCwiZXhwIjoxNzYwMTE0MDAwfQ.kQUNQA3m7Rjo_6SWdK4iy1N9nuRo0ocvZa2xcP4ov7NoedPHMqndYtrTBmjYKmTxCzsABSoZjPKnbywhQmjUcg")
    String accessToken,

    @Schema(description = "Токен обновления (Refresh Token)",
            example = "a62cc616-b632-4b28-bff2-5eda3aa2460c")
    String refreshToken
) {
}
