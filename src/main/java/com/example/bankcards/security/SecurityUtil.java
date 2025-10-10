package com.example.bankcards.security;

import com.example.bankcards.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-11 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public class SecurityUtil {

    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserEntity) {
            return (UserEntity) authentication;
        } else {
            throw new IllegalArgumentException("Authentication class must implement UserEntity");
        }
    }
}
