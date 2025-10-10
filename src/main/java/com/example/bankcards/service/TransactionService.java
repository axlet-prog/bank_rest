package com.example.bankcards.service;

import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.security.SecurityUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public void makeTransaction(TransactionRequest request) {
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than to zero");
        }

        UserEntity currentUser = SecurityUtil.getCurrentUser();

        CardEntity cardFrom = cardRepository.findById(request.cardIdFrom()).orElseThrow(
            () -> new RuntimeException()
        );

        checkUserCard(currentUser, cardFrom);

        CardEntity cardTo = cardRepository.findById(request.cardIdTo()).orElseThrow(
            () -> new RuntimeException()
        );

        checkUserCard(currentUser, cardTo);

        if (cardFrom.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException();
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(request.amount()));
        cardTo.setBalance(cardTo.getBalance().add(request.amount()));

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        transactionRepository.save(TransactionEntity.builder()
                                       .cardTo(cardTo)
                                       .cardFrom(cardFrom)
                                       .value(request.amount())
                                       .build()
        );
    }

    private void checkUserCard(UserEntity user, CardEntity card) {
        if (!Objects.equals(user.getId(), card.getUser().getId())) {
            throw new RuntimeException();
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException();
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException();
        }
    }
}
