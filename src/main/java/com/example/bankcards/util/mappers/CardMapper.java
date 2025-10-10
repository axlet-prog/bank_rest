package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.entity.CardEntity;

public class CardMapper {

    public static CardResponseDto toDto(CardEntity entity) {
        if (entity == null) {
            return null;
        }

        // вызов сервиса для расшифровки номера карты.
        String cardNumber = entity.getCardNumberEncrypted();

        return new CardResponseDto(
            entity.getId(),
            entity.getUser().getId(),
            cardNumber,
            entity.getExpiryDate(),
            entity.getStatus(),
            entity.getBalance().doubleValue(),
            entity.getUpdatedDatetime(),
            entity.getCreatedDatetime()
        );
    }
}