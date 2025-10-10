package com.example.bankcards.controller;

import static com.example.bankcards.config.SecurityConfig.USER_PRE_AUTHORIZE;

import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-11 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize(USER_PRE_AUTHORIZE)
    @PostMapping("/")
    public ResponseEntity<Void> makeTransaction(
        @RequestBody TransactionRequest request
    ) {
        transactionService.makeTransaction(request);
        return ResponseEntity.ok().build();
    }
}
