package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.UserEntity;

public class UserMapper {

    public static UserResponseDto toDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return new UserResponseDto(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getRole(),
            userEntity.getUpdatedDatetime(),
            userEntity.getCreatedDatetime()
        );
    }
}
