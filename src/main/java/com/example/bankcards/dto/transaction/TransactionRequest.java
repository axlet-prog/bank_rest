package com.example.bankcards.dto.transaction;

import java.math.BigDecimal;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-11 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public record TransactionRequest(
    Long cardIdFrom,
    Long cardIdTo,
    BigDecimal amount
) {

}
