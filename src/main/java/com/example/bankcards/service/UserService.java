package com.example.bankcards.service;

import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.specifications.DateSpecifications;
import com.example.bankcards.repository.specifications.UserSpecification;
import com.example.bankcards.util.mappers.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SearchResponseDto<UserResponseDto> searchUsers(SearchRequest<UserSearchRequestFilters> searchRequest) {
        Specification<UserEntity> userSpecification = Specification.unrestricted();
        var filter = searchRequest.getFilter();
        if (filter != null) {
            userSpecification = userSpecification.and(
                UserSpecification.usernameStartsWith(filter.getUsernamePrefix())
            );

            userSpecification = userSpecification.and(
                UserSpecification.hasRoles(filter.getRoles())
            );

            userSpecification = userSpecification.and(
                DateSpecifications.createDateTimeBetween(filter.getCreateDateFrom(), filter.getCreateDateTo())
            );

            userSpecification = userSpecification.and(
                DateSpecifications.updateDateTimeBetween(filter.getUpdateDateFrom(), filter.getCreateDateTo())
            );
        }

        Page<UserEntity> usersPage = userRepository.findAll(userSpecification, searchRequest.getPageable());

        List<UserResponseDto> responseData = usersPage.get().map(UserMapper::toDto).toList();

        return SearchResponseDto.of(
            responseData,
            usersPage.getTotalElements(),
            searchRequest.getPageable()
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Transactional
    public UserResponseDto changeUserRole(Long userId, Role newRole) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Unable to find user with id: " + userId)
        );

        userEntity.setRole(newRole);
        userRepository.save(userEntity);

        return UserMapper.toDto(userEntity);
    }
}
