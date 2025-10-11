package com.example.bankcards.service;

import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.specifications.DateSpecifications;
import com.example.bankcards.repository.specifications.UserSpecification;
import com.example.bankcards.util.mappers.UserMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-10 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public SearchResponseDto<UserResponseDto> searchUsers(SearchRequest<UserSearchRequestFilters> searchRequest) {
        Specification<UserEntity> userSpecification = Specification.unrestricted();
        if (searchRequest.getFilter() != null) {
            var filter = searchRequest.getFilter();
            if (filter.getUsernamePrefix() != null) {
                userSpecification = userSpecification.and(
                    UserSpecification.usernameStartsWith(filter.getUsernamePrefix())
                );
            }

            if (filter.getRoles() != null) {
                userSpecification = userSpecification.and(
                    UserSpecification.hasRoles(filter.getRoles())
                );
            }

            if (filter.getCreateDateFrom() != null || filter.getCreateDateTo() != null) {
                userSpecification = userSpecification.and(
                    DateSpecifications.createDateTimeBetween(filter.getCreateDateFrom(), filter.getCreateDateTo())
                );
            }

            if (filter.getUpdateDateFrom() != null || filter.getUpdateDateTo() != null) {
                userSpecification = userSpecification.and(
                    DateSpecifications.updateDateTimeBetween(filter.getUpdateDateFrom(), filter.getCreateDateTo())
                );
            }
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

        RoleEntity newRoleEntity = roleRepository.findByRoleName(newRole).orElseThrow(
            () -> new IllegalArgumentException("Unable to find role: " + newRole.name())
        );

        Set<RoleEntity> roles = userEntity.getRoles();
        roles.add(newRoleEntity);
        userEntity.setRoles(roles);
        userRepository.save(userEntity);

        return UserMapper.toDto(userEntity);
    }
}
