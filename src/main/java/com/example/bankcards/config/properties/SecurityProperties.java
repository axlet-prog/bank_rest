package com.example.bankcards.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-09 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "application.security")
public record SecurityProperties(
    JwtProperties jwt,
    DataEncoding dataEncoding,
    RefreshToken refreshToken
) {

    public record JwtProperties(
        int expiresInMillis,
        String singingKey
    ) { }

    public record DataEncoding(
        String cardNumberSecretKey,
        String refreshTokenSecretKey
    ) { }

    public record RefreshToken(
        long expiresInSeconds
    ) { }
}
