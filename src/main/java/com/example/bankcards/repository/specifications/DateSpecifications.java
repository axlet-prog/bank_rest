package com.example.bankcards.repository.specifications;

import com.example.bankcards.entity.CreateOnlyEntity;
import com.example.bankcards.entity.UpdateEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;


public final class DateSpecifications {

    private DateSpecifications() { }

    public static <T> Specification<T> dateTimeBetween(String dateFieldName, LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) -> {
            if (from != null && to != null) {
                return criteriaBuilder.between(root.get(dateFieldName), from, to);
            } else if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), from);
            } else if (to != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(dateFieldName), to);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }


    public static <T extends CreateOnlyEntity> Specification<T> createDateTimeBetween(
        LocalDateTime from, LocalDateTime to) {
        return dateTimeBetween(CreateOnlyEntity.createdFieldName, from, to);
    }

    public static <T extends UpdateEntity> Specification<T> updateDateTimeBetween(
        LocalDateTime from, LocalDateTime to) {
        return dateTimeBetween(UpdateEntity.updatedFieldName, from, to);
    }
}