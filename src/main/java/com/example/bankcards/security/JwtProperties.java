package com.example.bankcards.security;

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
@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
    int expiresInMillis,
    String singingKey
) { }
