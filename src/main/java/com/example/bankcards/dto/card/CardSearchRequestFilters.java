package com.example.bankcards.dto.card;

import com.example.bankcards.dto.search.SearchRequestFilter;
import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CardSearchFilters", description = "Фильтры для поиска банковских карт")
public class CardSearchRequestFilters implements SearchRequestFilter {

    @Schema(description = "Идентификатор владельца карты для фильтрации", example = "42")
    private Long ownerId;

    @Schema(description = "Список статусов карт для фильтрации. Будут найдены карты с любым из указанных статусов.",
            example = "[\"ACTIVE\", \"BLOCKED\"]")
    private List<CardStatus> cardStatuses;

    @Schema(description = "Начало периода для фильтрации по дате последнего обновления",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime updateDateFrom;

    @Schema(description = "Конец периода для фильтрации по дате последнего обновления",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime updateDateTo;

    @Schema(description = "Начало периода для фильтрации по дате создания",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime createDateFrom;

    @Schema(description = "Конец периода для фильтрации по дате создания",
            example = "2025-10-10T19:08:36.907Z")
    private LocalDateTime createDateTo;
}
