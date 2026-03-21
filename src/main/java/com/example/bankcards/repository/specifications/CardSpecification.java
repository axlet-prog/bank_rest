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


public class CardSpecification {


    private CardSpecification() { }

    public static Specification<CardEntity> hasOwnerId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<CardEntity, UserEntity> userJoin = root.join(CardEntity_.USER);
            return criteriaBuilder.equal(userJoin.get(UserEntity_.ID), userId);
        };
    }

    public static Specification<CardEntity> hasStatuses(List<CardStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(statuses)) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }
}
