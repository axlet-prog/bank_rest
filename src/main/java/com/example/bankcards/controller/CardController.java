package com.example.bankcards.controller;

import static com.example.bankcards.config.SecurityConfig.ADMIN_PRE_AUTHORIZE;
import static com.example.bankcards.config.SecurityConfig.USER_PRE_AUTHORIZE;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchRequestFilters;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.PatchCardRequest;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.service.CardService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/")
    public ResponseEntity<SearchResponseDto<CardResponseDto>> getCards(
        @RequestBody SearchRequest<CardSearchRequestFilters> request
    ) {
        return ResponseEntity.ok(cardService.getCards(request));
    }

    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @PostMapping("/")
    public ResponseEntity<CardResponseDto> createCard(
        @RequestBody CreateCardRequest createCardRequest
    ) {
        return ResponseEntity.ok(cardService.createCard(createCardRequest));
    }

    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @PatchMapping("/{id}")
    public ResponseEntity<CardResponseDto> changeCard(
        @PathVariable("id") Long cardId,
        @RequestBody PatchCardRequest request
    ) {
        return ResponseEntity.ok(cardService.updateCard(cardId, request));
    }

    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
        @PathVariable("id") Long cardId
    ) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(USER_PRE_AUTHORIZE)
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        return ResponseEntity.ok(cardService.getTotalBalance());
    }

}
