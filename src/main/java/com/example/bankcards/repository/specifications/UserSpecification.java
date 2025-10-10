package com.example.bankcards.repository.specifications;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.RoleEntity_;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.UserEntity_;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Join;
import java.util.List;
import java.util.stream.Collectors;
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
public class UserSpecification {

    private UserSpecification() { }

    public static Specification<UserEntity> usernameStartsWith(String prefix) {
        return (root, query, criteriaBuilder) -> {
            if (prefix != null && !prefix.isBlank()) {
                return criteriaBuilder.conjunction();
            } else {

                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(UserEntity_.USERNAME)),
                    prefix.toLowerCase() + "%"
                );
            }
        };
    }

    public static Specification<UserEntity> hasRoles(List<String> roleNames) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(roleNames)) {
                return criteriaBuilder.conjunction();
            }

            List<Role> roles;
            try {
                roles = roleNames.stream()
                    .map(String::toUpperCase) // Приводим к верхнему регистру, т.к. имена enum обычно в нем
                    .map(Role::valueOf)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // TODO(выбросить исключение)
                return criteriaBuilder.disjunction();
            }

            Join<UserEntity, RoleEntity> rolesJoin = root.join(RoleEntity.class.getAnnotation(Table.class).name());

            query.distinct(true);
            return rolesJoin.get(RoleEntity_.ROLE_NAME).in(roles);
        };
    }
}
