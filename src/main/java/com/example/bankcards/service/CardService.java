package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchRequestFilters;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.PatchCardRequest;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CardService {

    SearchResponseDto<CardResponseDto> searchCards(SearchRequest<CardSearchRequestFilters> searchRequest);

    List<CardResponseDto> getCards();

    CardResponseDto createCard(CreateCardRequest request);

    CardResponseDto updateCard(Long cardId, PatchCardRequest request);

    void deleteCard(Long cardId);

    BigDecimal getTotalBalance();

}
