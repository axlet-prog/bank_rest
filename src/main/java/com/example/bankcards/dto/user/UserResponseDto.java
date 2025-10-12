package com.example.bankcards.dto.user;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;


@Schema(description = "Ответ с информацией о пользователе")
public record UserResponseDto(

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    Long id,

    @Schema(description = "Имя пользователя (логин)", example = "john_doe")
    String username,

    @Schema(description = "Список ролей, назначенных пользователю")
    Role role,

    @Schema(description = "Дата и время последнего обновления данных пользователя")
    LocalDateTime updatedDateTime,

    @Schema(description = "Дата и время регистрации пользователя")
    LocalDateTime createdDateTime
) {
}
