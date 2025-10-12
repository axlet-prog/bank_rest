package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.BusinessLogicException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.security.SecurityUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceDefault transactionService;

    @Test
    void makeTransaction_Success_WhenAllConditionsAreMet() {
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);

        CardEntity cardFrom = CardEntity.builder()
            .id(10L)
            .user(currentUser)
            .balance(new BigDecimal("1000.00"))
            .status(CardStatus.ACTIVE)
            .expiryDate(LocalDate.now().plusYears(1))
            .build();

        CardEntity cardTo = CardEntity.builder()
            .id(20L)
            .user(currentUser)
            .balance(new BigDecimal("500.00"))
            .status(CardStatus.ACTIVE)
            .expiryDate(LocalDate.now().plusYears(1))
            .build();

        TransactionRequest request = new TransactionRequest(10L, 20L, new BigDecimal("100.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(currentUser);

            when(cardRepository.findById(10L)).thenReturn(Optional.of(cardFrom));
            when(cardRepository.findById(20L)).thenReturn(Optional.of(cardTo));

            transactionService.makeTransaction(request);

            ArgumentCaptor<CardEntity> cardCaptor = ArgumentCaptor.forClass(CardEntity.class);
            verify(cardRepository, times(2)).save(cardCaptor.capture());

            CardEntity savedCardFrom = cardCaptor.getAllValues().get(0);
            CardEntity savedCardTo = cardCaptor.getAllValues().get(1);

            assertEquals(0, new BigDecimal("900.00").compareTo(savedCardFrom.getBalance()));
            assertEquals(0, new BigDecimal("600.00").compareTo(savedCardTo.getBalance()));

            ArgumentCaptor<TransactionEntity> transactionCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionRepository, times(1)).save(transactionCaptor.capture());

            TransactionEntity savedTransaction = transactionCaptor.getValue();
            assertEquals(cardFrom, savedTransaction.getCardFrom());
            assertEquals(cardTo, savedTransaction.getCardTo());
            assertEquals(0, request.amount().compareTo(savedTransaction.getValue()));
        }
    }

    @Test
    void makeTransaction_ThrowsBusinessLogicException_WhenInsufficientFunds() {
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);

        CardEntity cardFrom = CardEntity.builder()
            .id(10L)
            .user(currentUser)
            .balance(new BigDecimal("50.00"))
            .status(CardStatus.ACTIVE)
            .expiryDate(LocalDate.now().plusYears(1))
            .build();

        CardEntity cardTo = CardEntity.builder()
            .id(20L)
            .user(currentUser)
            .balance(new BigDecimal("500.00"))
            .status(CardStatus.ACTIVE)
            .expiryDate(LocalDate.now().plusYears(1))
            .build();

        TransactionRequest request = new TransactionRequest(
            10L, 20L, new BigDecimal("100.00")); // Хотим списать больше, чем есть

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(currentUser);
            when(cardRepository.findById(10L)).thenReturn(Optional.of(cardFrom));
            when(cardRepository.findById(20L)).thenReturn(Optional.of(cardTo));

            BusinessLogicException exception = assertThrows(
                BusinessLogicException.class, () -> {
                    transactionService.makeTransaction(request);
                }
            );

            assertEquals("There are not enough money in the balance of the card", exception.getMessage());

            verify(cardRepository, never()).save(any(CardEntity.class));
            verify(transactionRepository, never()).save(any(TransactionEntity.class));
        }
    }

    @Test
    void makeTransaction_ThrowsIllegalArgumentException_WhenCardDoesNotBelongToUser() {
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);

        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);

        CardEntity cardFrom = CardEntity.builder()
            .id(10L)
            .user(anotherUser)
            .balance(new BigDecimal("1000.00"))
            .status(CardStatus.ACTIVE)
            .expiryDate(LocalDate.now().plusYears(1))
            .build();

        TransactionRequest request = new TransactionRequest(10L, 20L, new BigDecimal("100.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(currentUser);
            when(cardRepository.findById(10L)).thenReturn(Optional.of(cardFrom));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    transactionService.makeTransaction(request);
                }
            );

            assertEquals("User cards don't match", exception.getMessage());
        }
    }
}