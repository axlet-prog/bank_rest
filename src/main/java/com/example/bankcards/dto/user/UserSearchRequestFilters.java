package com.example.bankcards.dto.user;

import com.example.bankcards.dto.search.SearchRequestFilter;
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
@Schema(name = "", description = "")
public class UserSearchRequestFilters implements SearchRequestFilter {

    private String usernamePrefix;

    private List<String> roles;

    private LocalDateTime updateDateFrom;

    private LocalDateTime updateDateTo;

    private LocalDateTime createDateFrom;

    private LocalDateTime createDateTo;
}

