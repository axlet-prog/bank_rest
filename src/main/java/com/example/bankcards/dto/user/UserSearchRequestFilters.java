package com.example.bankcards.dto.user;

import com.example.bankcards.dto.search.SearchRequestFilter;
import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserSearchFilters", description = "Фильтры для поиска пользователей")
public class UserSearchRequestFilters implements SearchRequestFilter {

    @Schema(description = "Префикс имени пользователя для поиска",
            example = "john")
    private String usernamePrefix;

    @Schema(
        description = "Список ролей для фильтрации. Будут найдены пользователи, обладающие любой из указанных ролей.")
    private List<Role> roles;

    @Schema(description = "Начало периода для фильтрации по дате последнего обновления",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime updateDateFrom;

    @Schema(description = "Конец периода для фильтрации по дате последнего обновления",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime updateDateTo;

    @Schema(description = "Начало периода для фильтрации по дате регистрации",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime createDateFrom;

    @Schema(description = "Конец периода для фильтрации по дате регистрации",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime createDateTo;
}

