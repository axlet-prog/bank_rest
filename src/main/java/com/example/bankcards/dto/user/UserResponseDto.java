package com.example.bankcards.dto.user;

import com.example.bankcards.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public record UserResponseDto(
    Long id,
    String username,
    List<Role> roles,
    LocalDateTime updatedDateTime,
    LocalDateTime createdDateTime
) {
}
