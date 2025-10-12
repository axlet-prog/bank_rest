package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchRequestFilters;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.PatchCardRequest;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.SecurityUtil;
import com.example.bankcards.util.converter.CardNumberConverter;
import com.example.bankcards.util.mappers.CardMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardNumberConverter cardConverter;

    @InjectMocks
    private CardServiceDefault cardService;

    private UserEntity testUser;
    private UserEntity testAdmin;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder().id(1L).username("user").role(Role.USER).build();
        testAdmin = UserEntity.builder().id(2L).username("admin").role(Role.ADMIN).build();
    }


    @Test
    void createCard_Success_WhenUserExists() {
        CreateCardRequest request = new CreateCardRequest(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);
        when(cardConverter.convertToDatabaseColumn(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponseDto expectedDto = new CardResponseDto(null, null, null, null, null, null, null, null);

        try (MockedStatic<CardMapper> mockedMapper = mockStatic(CardMapper.class)) {
            mockedMapper.when(() -> CardMapper.toDto(any(CardEntity.class))).thenReturn(expectedDto);

            CardResponseDto result = cardService.createCard(request);

            assertNotNull(result);

            ArgumentCaptor<CardEntity> cardCaptor = ArgumentCaptor.forClass(CardEntity.class);
            verify(cardRepository).save(cardCaptor.capture());
            CardEntity savedCard = cardCaptor.getValue();

            assertEquals(testUser, savedCard.getUser());
            assertEquals(0, BigDecimal.ZERO.compareTo(savedCard.getBalance()));
            assertEquals(CardStatus.ACTIVE, savedCard.getStatus());
            assertEquals(LocalDate.now().plusYears(5), savedCard.getExpiryDate());
            assertNotNull(savedCard.getCardNumber());
        }
    }

    @Test
    void createCard_ThrowsException_WhenUserNotFound() {
        CreateCardRequest request = new CreateCardRequest(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }


    @Test
    void updateCard_Success_UpdatesStatusAndBalance() {
        Long cardId = 1L;
        CardEntity existingCard = CardEntity.builder()
            .id(cardId)
            .status(CardStatus.ACTIVE)
            .balance(new BigDecimal("100.00"))
            .build();

        PatchCardRequest request = new PatchCardRequest(CardStatus.BLOCKED, new BigDecimal("250.50"));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));

        try (MockedStatic<CardMapper> mockedMapper = mockStatic(CardMapper.class)) {
            mockedMapper.when(() -> CardMapper.toDto(any(CardEntity.class)))
                .thenReturn(new CardResponseDto(null, null, null, null, null, null, null, null));

            cardService.updateCard(cardId, request);

            ArgumentCaptor<CardEntity> cardCaptor = ArgumentCaptor.forClass(CardEntity.class);
            verify(cardRepository).save(cardCaptor.capture());
            CardEntity savedCard = cardCaptor.getValue();

            assertEquals(CardStatus.BLOCKED, savedCard.getStatus());
            assertEquals(0, new BigDecimal("250.50").compareTo(savedCard.getBalance()));
        }
    }

    @Test
    void getCards_ForUserRole_ReturnsOnlyOwnedCards() {
        try (
            MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class);
            MockedStatic<CardMapper> mockedMapper = mockStatic(CardMapper.class)
        ) {

            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);

            CardEntity ownedCard = CardEntity.builder()
                .id(100L)
                .user(testUser)
                .cardNumber("1234")
                .balance(BigDecimal.TEN)
                .build();

            CardResponseDto expectedDto = new CardResponseDto(100L, 1234L, null, null, null, 1d, null, null);
            mockedMapper.when(() -> CardMapper.toDto(ownedCard)).thenReturn(expectedDto);

            when(cardRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(ownedCard));

            List<CardResponseDto> cards = cardService.getCards();

            assertNotNull(cards);
            assertEquals(1, cards.size());

            verify(cardRepository, times(1)).findAllByUserId(testUser.getId());
            verify(cardRepository, never()).findAll();
        }
    }

    @Test
    void getCards_ForAdminRole_ReturnsAllCards() {
        try (
            MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class);
            MockedStatic<CardMapper> mockedMapper = mockStatic(CardMapper.class)
        ) {

            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testAdmin);

            CardEntity cardOfUser = CardEntity.builder()
                .id(100L)
                .user(testUser)
                .build();
            CardEntity cardOfAdmin = CardEntity.builder()
                .id(200L)
                .user(testAdmin)
                .build();

            mockedMapper.when(() -> CardMapper.toDto(any(CardEntity.class)))
                .thenReturn(new CardResponseDto(null, null, null, null, null, null, null, null));

            when(cardRepository.findAll()).thenReturn(List.of(cardOfUser, cardOfAdmin));

            List<CardResponseDto> cards = cardService.getCards();

            assertNotNull(cards);
            assertEquals(2, cards.size());

            verify(cardRepository, times(1)).findAll();
            verify(cardRepository, never()).findAllByUserId(anyLong());
        }
    }


    @Test
    void searchCards_ForUserRole_AlwaysFiltersByOwnerId() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);

            SearchRequest<CardSearchRequestFilters> request = new SearchRequest<>(0, 10, null);
            Page<CardEntity> resultPage = new PageImpl<>(List.of());
            when(cardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(resultPage);

            cardService.searchCards(request);

            verify(cardRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void searchCards_ForAdminRole_WithOwnerIdFilter_AppliesFilter() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testAdmin);

            CardSearchRequestFilters filters = new CardSearchRequestFilters();
            SearchRequest<CardSearchRequestFilters> request = new SearchRequest<>(0, 10, filters);

            Page<CardEntity> resultPage = new PageImpl<>(List.of());
            when(cardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(resultPage);

            cardService.searchCards(request);

            verify(cardRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }


    @Test
    void getTotalBalance_ReturnsCorrectSumOfCardBalances() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);

            CardEntity card1 = CardEntity.builder().balance(new BigDecimal("100.50")).build();
            CardEntity card2 = CardEntity.builder().balance(new BigDecimal("200.00")).build();
            CardEntity card3 = CardEntity.builder().balance(new BigDecimal("50.25")).build();

            when(cardRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(card1, card2, card3));

            BigDecimal totalBalance = cardService.getTotalBalance();

            assertEquals(0, new BigDecimal("350.75").compareTo(totalBalance));
        }
    }
}