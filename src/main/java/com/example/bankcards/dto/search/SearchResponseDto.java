package com.example.bankcards.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Общая DTO ответа на поисковый запрос")
public final class SearchResponseDto<T> {

    @Schema(description = "Ответ с данными.")
    private List<T> data;

    @Schema(description = "Размер страницы")
    private int size;

    @Schema(description = "Страница")
    private int page;

    @Schema(description = "Всего результатов")
    private long totalElements;

    @Schema(description = "Всего страниц")
    private long totalPages;

    public static <T> SearchResponseDto<T> of(
        final List<T> data,
        final long totalElements,
        final Pageable pageable
    ) {
        SearchResponseDto<T> responseDto = new SearchResponseDto<>();
        responseDto.setPage(pageable.getPageNumber());
        responseDto.setData(data);
        responseDto.setSize(pageable.getPageSize());
        responseDto.setTotalElements(totalElements);
        responseDto.setTotalPages(Math.ceilDiv(totalElements, pageable.getPageSize()));
        return responseDto;
    }
}
