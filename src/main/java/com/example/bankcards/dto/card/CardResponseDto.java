package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Schema(description = "Ответ с полной информацией о банковской карте")
public record CardResponseDto(

    @Schema(description = "Уникальный идентификатор карты", example = "101")
    Long cardId,

    @Schema(description = "Идентификатор владельца карты", example = "42")
    Long ownerId,

    @Schema(description = "Номер карты (маскированный)", example = "************1234")
    String cardNumber,

    @Schema(description = "Дата окончания срока действия карты", example = "2028-12-01")
    LocalDate expirationDate,

    @Schema(description = "Текущий статус карты", example = "ACTIVE")
    CardStatus cardStatus,

    @Schema(description = "Текущий баланс на карте", example = "15500.75")
    Double balance,

    @Schema(description = "Дата и время последнего обновления данных карты")
    LocalDateTime updatedDateTime,

    @Schema(description = "Дата и время создания карты")
    LocalDateTime createdDateTime
) {
}