package com.example.bankcards.repository.specifications;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardEntity_;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.UserEntity_;
import jakarta.persistence.criteria.Join;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public class CardSpecification {

    /**
     * Приватный конструктор для предотвращения инстанцирования утилитарного класса.
     */
    private CardSpecification() {
    }

    /**
     * Создает спецификацию для фильтрации карт по идентификатору их владельца (UserEntity.id).
     *
     * @param userId Идентификатор пользователя. Если null, фильтр не применяется.
     * @return {@link Specification<CardEntity>}
     */
    public static Specification<CardEntity> hasOwnerId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                // Если ID пользователя не указан, не применяем фильтр
                return criteriaBuilder.conjunction();
            }

            // Создаем JOIN с сущностью UserEntity, чтобы получить доступ к ее полям
            Join<CardEntity, UserEntity> userJoin = root.join(CardEntity_.USER);

            // Создаем предикат, который сравнивает поле "id" в UserEntity с переданным userId
            return criteriaBuilder.equal(userJoin.get(UserEntity_.ID), userId);
        };
    }

    /**
     * Создает спецификацию для фильтрации карт по одному или нескольким статусам.
     *
     * @param statuses Список статусов {@link CardStatus}. Если список null или пуст, фильтр не применяется.
     * @return {@link Specification<CardEntity>}
     */
    public static Specification<CardEntity> hasStatuses(List<CardStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            // Проверяем, что список статусов не пуст
            if (CollectionUtils.isEmpty(statuses)) {
                // Если список пуст, не применяем фильтр
                return criteriaBuilder.conjunction();
            }

            // Создаем предикат "IN", который проверяет, что поле "status"
            // содержится в переданном списке статусов
            return root.get("status").in(statuses);
        };
    }
}
