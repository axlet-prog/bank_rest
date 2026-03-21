package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshTokenEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHashAndExpiryDateAfter(String tokenHash, LocalDateTime expiryDate);
}
