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
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.specifications.CardSpecification;
import com.example.bankcards.security.SecurityUtil;
import com.example.bankcards.util.mappers.CardMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SearchResponseDto<CardResponseDto> getCards(SearchRequest<CardSearchRequestFilters> searchRequest) {
        UserEntity currentUser = SecurityUtil.getCurrentUser();
        Set<Role> roles = currentUser.getRoles().stream().map(RoleEntity::getRoleName).collect(Collectors.toSet());
        if (!roles.contains(Role.ADMIN)) {
            searchRequest.getFilter().setOwnerId(currentUser.getId());
        }

        var filter = searchRequest.getFilter();

        Specification<CardEntity> cardSpecification = Specification.unrestricted();

        cardSpecification = cardSpecification.and(
            CardSpecification.hasOwnerId(searchRequest.getFilter().getOwnerId())
        );

        cardSpecification = cardSpecification.and(
            CardSpecification.hasStatuses(filter.getCardStatuses())
        );

        Page<CardEntity> cardsPage = cardRepository.findAll(cardSpecification, searchRequest.getPageable());

        List<CardResponseDto> responseData = cardsPage.get().map(CardMapper::toDto).toList();

        return SearchResponseDto.of(
            responseData,
            cardsPage.getTotalElements(),
            searchRequest.getPageable()
        );
    }

    @Transactional
    public CardResponseDto createCard(CreateCardRequest request) {
        UserEntity owner = userRepository.findById(request.userId()).orElseThrow(
            () -> new RuntimeException()
        );

        String cardNumber = generateCardNumber();

        CardEntity cardEntity = CardEntity.builder()
            .cardNumberEncrypted(cardNumber)
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
            () -> new RuntimeException()
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
            () -> new RuntimeException()
        );

        cardRepository.deleteById(cardEntity.getId());
    }

    @Transactional
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
        sbNumber.append(String.format("%04d", rnd.nextInt(0, 1000)));

        for (int i = 0; i < 3; i++) {
            sbNumber.append(String.format("%04d", rnd.nextInt(0, 10000)));
        }
        if (cardRepository.existsByCardNumberEncrypted(sbNumber.toString())) {
            return generateCardNumber();
        } else {
            return sbNumber.toString();
        }

    }
}
