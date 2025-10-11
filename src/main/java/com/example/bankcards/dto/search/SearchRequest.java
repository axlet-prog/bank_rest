package com.example.bankcards.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
@Schema(description = "Общая DTO поискового запроса")
public final class SearchRequest<T extends SearchRequestFilter> {

    @Schema(description = "Страница. Начинается с 0.")
    private int page;

    @Schema(description = "Размер страницы")
    private int size;

    @Schema(description = "Фильтры")
    private T filter;

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

    public SearchRequest(int page, int size, T filter) {
        this.page = page;
        this.size = size;
        this.filter = filter;
    }
}
