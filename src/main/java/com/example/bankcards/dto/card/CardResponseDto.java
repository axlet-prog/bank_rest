package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public record CardResponseDto(
    Long cardId,
    Long ownerId,
    String cardNumber,
    LocalDate expirationDate,
    CardStatus cardStatus,
    Double balance,
    LocalDateTime updatedDateTime,
    LocalDateTime createdDateTime
) {
}
