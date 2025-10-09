package com.example.bankcards.service;

import com.example.bankcards.dto.auth.JwtRefreshRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.entity.RefreshTokenEntity;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-09 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceDefault {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManager authenticationManager;

    // Регистрация пользователя
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        RoleEntity userRole = roleRepository.findByRoleName(Role.USER);
        var client = UserEntity.builder()
            .username(request.username())
            .passwordHash(passwordEncoder.encode(request.password()))
            .roles(List.of(userRole))
            .build();

        userRepository.save(client);

        var jwt = jwtService.generateToken(client);

        var refreshToken = generateRefreshToken(client);
        refreshTokenRepository.save(refreshToken);

        return new JwtResponse(
            jwt,
            refreshToken.getTokenHash()
        );
    }

    // Аунтификация пользователя
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        ));

        // TODO(Сделать норм обработку ошибок)
        var user = userRepository.findByUsername(request.username()).get();

        var jwt = jwtService.generateToken(user);
        var refreshToken = generateRefreshToken(user);

        refreshTokenRepository.save(refreshToken);

        return new JwtResponse(
            jwt,
            refreshToken.getTokenHash()
        );
    }


    // Обновление токена
    public JwtResponse refresh(JwtRefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AccessDeniedException("Refresh token is missing");
        }

        RefreshTokenEntity oldToken = refreshTokenRepository.findByTokenHash(refreshToken).orElseThrow(
            () -> new AccessDeniedException("Refresh token not found")
        );

        var jwt = jwtService.generateToken(oldToken.getUser());
        RefreshTokenEntity newRefreshToken = updateRefreshToken(oldToken);

        return new JwtResponse(
            jwt,
            newRefreshToken.getTokenHash()
        );
    }

    private RefreshTokenEntity generateRefreshToken(UserEntity user) {
        UUID tokenUUID = UUID.randomUUID();
        return RefreshTokenEntity.builder()
            .user(user)
            .tokenHash(tokenUUID.toString())
            .expiryDate(LocalDateTime.now().plusSeconds(100000L * 60 * 24 * 20))
            .build();
    }

    private RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken) {
        RefreshTokenEntity oldToken = refreshTokenRepository.findById(refreshToken.getRefreshTokenId())
            .orElseThrow(() -> new AccessDeniedException("Invalid refresh token"));
        if (oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Expired refresh token");
        }

        RefreshTokenEntity newToken = generateRefreshToken(oldToken.getUser());
        refreshTokenRepository.delete(oldToken);
        return refreshTokenRepository.save(newToken);
    }
}
