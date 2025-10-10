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
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "", description = "")
public class CardSearchRequestFilters implements SearchRequestFilter {

    private Long ownerId;

    private List<CardStatus> cardStatuses;

    private LocalDateTime updateDateFrom;

    private LocalDateTime updateDateTo;

    private LocalDateTime createDateFrom;

    private LocalDateTime createDateTo;
}

