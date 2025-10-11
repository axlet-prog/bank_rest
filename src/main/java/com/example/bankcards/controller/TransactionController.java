package com.example.bankcards.controller;

import static com.example.bankcards.config.SecurityConfig.USER_PRE_AUTHORIZE;

import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Проведение транзакций", description = "Эндпоинт для выполнения денежных переводов")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(description = "Выполнение денежного перевода с одной карты на другую.")
    @PreAuthorize(USER_PRE_AUTHORIZE)
    @PostMapping("/")
    public ResponseEntity<Void> makeTransaction(
        @RequestBody TransactionRequest request
    ) {
        transactionService.makeTransaction(request);
        return ResponseEntity.ok().build();
    }
}
