package com.example.bankcards.repository.specifications;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.UserEntity_;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

public class UserSpecification {

    private UserSpecification() { }

    public static Specification<UserEntity> usernameStartsWith(String prefix) {
        return (root, query, criteriaBuilder) -> {
            if (prefix == null || prefix.isBlank()) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(UserEntity_.USERNAME)),
                    prefix.toLowerCase() + "%"
                );
            }
        };
    }

    public static Specification<UserEntity> hasRoles(List<Role> roles) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(roles)) {
                return criteriaBuilder.conjunction();
            }
            return root.get(UserEntity_.ROLE).in(roles);
        };
    }
}
