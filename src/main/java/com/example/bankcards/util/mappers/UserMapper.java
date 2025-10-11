package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
