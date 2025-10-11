package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.CardMaskUtil;

public class CardMapper {

    public static CardResponseDto toDto(CardEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CardResponseDto(
            entity.getId(),
            entity.getUser().getId(),
            CardMaskUtil.applyMask(entity.getCardNumber()),
            entity.getExpiryDate(),
            entity.getStatus(),
            entity.getBalance().doubleValue(),
            entity.getUpdatedDatetime(),
            entity.getCreatedDatetime()
        );
    }
}