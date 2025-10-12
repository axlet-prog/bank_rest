package com.example.bankcards.service;

import com.example.bankcards.config.properties.SecurityProperties;
import com.example.bankcards.dto.auth.JwtRefreshRequest;
import com.example.bankcards.dto.auth.JwtResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.entity.RefreshTokenEntity;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceDefault {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final SecurityProperties securityProperties;


    @Transactional
    public JwtResponse register(RegisterRequest request) {

        var user = UserEntity.builder()
            .username(request.username())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build();

        userRepository.save(user);
        RefreshTokenEntity refreshToken = generateRawRefreshToken(user);
        return saveRefreshTokenAndGetJwtResponse(refreshToken);
    }

    @Transactional
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        ));

        var user = userRepository.findByUsername(request.username()).orElseThrow(
            () -> new AccessDeniedException("User with username " + request.username() + " not found")
        );
        RefreshTokenEntity refreshToken = generateRawRefreshToken(user);
        return saveRefreshTokenAndGetJwtResponse(refreshToken);
    }

    @Transactional
    public JwtResponse refresh(JwtRefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AccessDeniedException("Refresh token is missing");
        }

        String encodedToken = DigestUtils.sha256Hex(refreshToken);
        RefreshTokenEntity oldToken = refreshTokenRepository.findByTokenHashAndExpiryDateAfter(
            encodedToken,
            LocalDateTime.now()
        ).orElseThrow(
            () -> new AccessDeniedException("Refresh token not found")
        );

        RefreshTokenEntity newRefreshToken = updateRefreshToken(oldToken);
        return saveRefreshTokenAndGetJwtResponse(newRefreshToken);
    }

    private JwtResponse saveRefreshTokenAndGetJwtResponse(RefreshTokenEntity refreshToken) {
        var jwt = jwtService.generateToken(refreshToken.getUser());
        JwtResponse response = new JwtResponse(
            jwt,
            refreshToken.getTokenHash()
        );

        refreshToken.setTokenHash(DigestUtils.sha256Hex(refreshToken.getTokenHash()));
        refreshTokenRepository.save(refreshToken);

        return response;
    }

    private RefreshTokenEntity generateRawRefreshToken(UserEntity user) {
        UUID tokenUUID = UUID.randomUUID();
        return RefreshTokenEntity.builder()
            .user(user)
            .tokenHash(tokenUUID.toString())
            .expiryDate(LocalDateTime.now().plusSeconds(securityProperties.refreshToken().expiresInSeconds()))
            .build();
    }

    private RefreshTokenEntity updateRefreshToken(RefreshTokenEntity refreshToken) {
        RefreshTokenEntity oldToken = refreshTokenRepository.findById(refreshToken.getRefreshTokenId())
            .orElseThrow(() -> new AccessDeniedException("Invalid refresh token"));
        if (oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Expired refresh token");
        }

        RefreshTokenEntity newToken = generateRawRefreshToken(oldToken.getUser());
        refreshTokenRepository.delete(oldToken);
        return newToken;
    }
}
