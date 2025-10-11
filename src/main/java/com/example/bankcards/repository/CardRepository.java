package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepository extends JpaRepository<CardEntity, Long>, JpaSpecificationExecutor<CardEntity> {

    Boolean existsByCardNumber(String cardNumber);

    List<CardEntity> findAllByUserId(Long userId);
}
