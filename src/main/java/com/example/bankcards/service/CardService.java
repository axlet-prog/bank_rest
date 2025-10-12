package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchRequestFilters;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.PatchCardRequest;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.specifications.CardSpecification;
import com.example.bankcards.security.SecurityUtil;
import com.example.bankcards.util.converter.CardNumberConverter;
import com.example.bankcards.util.mappers.CardMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberConverter cardConverter;

    @Transactional(readOnly = true)
    public SearchResponseDto<CardResponseDto> searchCards(SearchRequest<CardSearchRequestFilters> searchRequest) {
        UserEntity currentUser = SecurityUtil.getCurrentUser();

        var filter = searchRequest.getFilter();

        Specification<CardEntity> cardSpecification = Specification.unrestricted();

        if (currentUser.getRole().equals(Role.USER)) {
            cardSpecification = cardSpecification.and(
                CardSpecification.hasOwnerId(currentUser.getId())
            );
        }

        if (filter != null) {
            if (currentUser.getRole().equals(Role.ADMIN)) {
                cardSpecification = cardSpecification.and(
                    CardSpecification.hasOwnerId(searchRequest.getFilter().getOwnerId())
                );
            }

            cardSpecification = cardSpecification.and(
                CardSpecification.hasStatuses(filter.getCardStatuses())
            );
        }

        Page<CardEntity> cardsPage = cardRepository.findAll(cardSpecification, searchRequest.getPageable());

        List<CardResponseDto> responseData = cardsPage.get().map(CardMapper::toDto).toList();

        return SearchResponseDto.of(
            responseData,
            cardsPage.getTotalElements(),
            searchRequest.getPageable()
        );
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCards() {
        UserEntity currentUser = SecurityUtil.getCurrentUser();

        List<CardEntity> cards;
        if (currentUser.getRole().equals(Role.ADMIN)) {
            cards = cardRepository.findAll();
        } else {
            cards = cardRepository.findAllByUserId(currentUser.getId());
        }

        return cards.stream().map(CardMapper::toDto).toList();
    }

    @Transactional
    public CardResponseDto createCard(CreateCardRequest request) {
        UserEntity owner = userRepository.findById(request.userId()).orElseThrow(
            () -> new IllegalArgumentException("Unable to find user with id: " + request.userId())
        );

        String cardNumber = generateCardNumber();

        CardEntity cardEntity = CardEntity.builder()
            .cardNumber(cardNumber)
            .expiryDate(LocalDate.now().plusYears(5))
            .balance(BigDecimal.valueOf(0))
            .user(owner)
            .status(CardStatus.ACTIVE)
            .build();

        cardRepository.save(cardEntity);

        return CardMapper.toDto(cardEntity);
    }

    @Transactional
    public CardResponseDto updateCard(Long cardId, PatchCardRequest request) {
        CardEntity cardEntity = cardRepository.findById(cardId).orElseThrow(
            () -> new IllegalArgumentException("Unable to find card with id: " + cardId)
        );

        if (request.cardStatus() != null) {
            cardEntity.setStatus(request.cardStatus());
        }

        if (request.balance() != null && request.balance().compareTo(BigDecimal.valueOf(0)) >= 0) {
            cardEntity.setBalance(request.balance());
        }

        cardRepository.save(cardEntity);
        return CardMapper.toDto(cardEntity);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId).orElseThrow(
            () -> new IllegalArgumentException("Unable to find card with id: " + cardId)
        );

        cardRepository.deleteById(cardEntity.getId());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance() {
        UserEntity currentUser = SecurityUtil.getCurrentUser();
        List<CardEntity> cardEntities = cardRepository.findAllByUserId(currentUser.getId());
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (CardEntity cardEntity : cardEntities) {
            totalBalance = totalBalance.add(cardEntity.getBalance());
        }
        return totalBalance;
    }

    private String generateCardNumber() {
        Random rnd = new Random();
        StringBuilder sbNumber = new StringBuilder();
        sbNumber.append("2");
        sbNumber.append(String.format("%03d", rnd.nextInt(0, 1000)));

        for (int i = 0; i < 3; i++) {
            sbNumber.append(String.format("%04d", rnd.nextInt(0, 10000)));
        }
        String encodedNumber = cardConverter.convertToDatabaseColumn(sbNumber.toString());
        if (cardRepository.existsByCardNumber(encodedNumber)) {
            return generateCardNumber();
        } else {
            return sbNumber.toString();
        }
    }
}
