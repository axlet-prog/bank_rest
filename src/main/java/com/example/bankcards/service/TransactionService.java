package com.example.bankcards.service;

import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.BusinessLogicException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.security.SecurityUtil;
import com.example.bankcards.util.CardMaskUtil;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public void makeTransaction(TransactionRequest request) {
        UserEntity currentUser = SecurityUtil.getCurrentUser();

        CardEntity cardFrom = cardRepository.findById(request.cardIdFrom()).orElseThrow(
            () -> new IllegalArgumentException("Unable to find card with id: " + request.cardIdFrom())
        );

        checkUserCard(currentUser, cardFrom);

        CardEntity cardTo = cardRepository.findById(request.cardIdTo()).orElseThrow(
            () -> new IllegalArgumentException("Unable to find card with id: " + request.cardIdTo())
        );

        checkUserCard(currentUser, cardTo);

        if (cardFrom.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessLogicException("There are not enough money in the balance of the card");
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
            throw new IllegalArgumentException("User cards don't match");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessLogicException("Cards must be ACTIVE");
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("Card " + CardMaskUtil.applyMask(card.getCardNumber()) + " has expired");
        }
    }
}
