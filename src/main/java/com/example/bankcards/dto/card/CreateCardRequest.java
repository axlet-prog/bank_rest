package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


@Schema(description = "Запрос на создание новой банковской карты")
public record CreateCardRequest(

    @NotNull
    @Schema(description = "Идентификатор пользователя, для которого создается карта",
            example = "123")
    Long userId
) {
}
