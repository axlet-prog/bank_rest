package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
public class UserMapper {

    public static UserResponseDto toDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        List<Role> roles = userEntity.getRoles() == null
                           ? Collections.emptyList()
                           : userEntity.getRoles().stream()
                               .map(RoleEntity::getRoleName)
                               .collect(Collectors.toList());

        return new UserResponseDto(
            userEntity.getId(),
            userEntity.getUsername(),
            roles,
            userEntity.getUpdatedDatetime(),
            userEntity.getCreatedDatetime()
        );
    }
}
