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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
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


@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Управление банковскими картами", description = "Эндпоинты для создания, поиска и управления картами")
public class CardController {

    private final CardService cardService;

    @Operation(description = "Поиск карт с использованием фильтров и пагинации.")
    @PostMapping("/search")
    public ResponseEntity<SearchResponseDto<CardResponseDto>> searchCards(
        @Valid @RequestBody SearchRequest<CardSearchRequestFilters> request
    ) {
        return ResponseEntity.ok(cardService.searchCards(request));
    }

    @Operation(description = "Получение списка карт.")
    @GetMapping("")
    public ResponseEntity<List<CardResponseDto>> getCards() {
        return ResponseEntity.ok(cardService.getCards());
    }

    @Operation(description = "Создание новой банковской карты. Требуются права администратора.")
    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @PostMapping("")
    public ResponseEntity<CardResponseDto> createCard(
        @Valid @RequestBody CreateCardRequest createCardRequest
    ) {
        return ResponseEntity.ok(cardService.createCard(createCardRequest));
    }

    @Operation(description = "Частичное обновление данных карты по ее ID. Требуются права администратора.")
    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @PatchMapping("/{id}")
    public ResponseEntity<CardResponseDto> changeCard(
        @Parameter(description = "Идентификатор обновляемой карты", required = true)
        @PathVariable("id") Long cardId,
        @RequestBody PatchCardRequest request
    ) {
        return ResponseEntity.ok(cardService.updateCard(cardId, request));
    }

    @Operation(description = "Удаление карты по ее ID. Требуются права администратора.")
    @PreAuthorize(ADMIN_PRE_AUTHORIZE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
        @Parameter(description = "Идентификатор удаляемой карты", required = true)
        @PathVariable("id") Long cardId
    ) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        description = "Получение общего баланса по всем картам текущего пользователя. Требуются права пользователя.")
    @PreAuthorize(USER_PRE_AUTHORIZE)
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        return ResponseEntity.ok(cardService.getTotalBalance());
    }
}
