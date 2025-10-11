package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@Schema(description = "Запрос на частичное обновление данных банковской карты (PATCH).")
public record PatchCardRequest(

    @Schema(description = "Новый статус для карты. Например, для ее блокировки.",
            example = "ACTIVE")
    CardStatus cardStatus,
    // Test purposes
    @Schema(description = "Новое значение баланса на карте.",
            example = "25000.77")
    BigDecimal balance
) {
}
