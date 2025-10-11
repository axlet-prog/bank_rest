package com.example.bankcards.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Запрос на проведение денежного перевода между картами")
public record TransactionRequest(

    @NotNull
    @Schema(description = "Идентификатор карты, с которой списываются средства (карта отправителя)",
            example = "101")
    Long cardIdFrom,

    @NotNull
    @Schema(description = "Идентификатор карты, на которую зачисляются средства (карта получателя)",
            example = "102")
    Long cardIdTo,

    @NotNull
    @Positive
    @Schema(description = "Сумма перевода. Должна быть положительным числом.",
            example = "1500.50")
    BigDecimal amount
) {
}
