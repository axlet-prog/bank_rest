package com.example.bankcards.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class SearchRequest<T extends SearchRequestFilter> {

    @Schema(description = "Страница. Начинается с 0.")
    private int page;

    @Schema(description = "Размер страницы")
    private int size;

    @Schema(description = "Фильтры")
    private T filter;


    public SearchRequest(int page, int size, T filter) {
        this.page = page;
        this.size = size;
        this.filter = filter;
    }
}
