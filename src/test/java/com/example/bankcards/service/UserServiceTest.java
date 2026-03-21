package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mappers.UserMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceDefault userService;


    @Test
    void changeUserRole_UserFound_ShouldUpdateRoleAndReturnDto() {
        Long userId = 1L;
        Role newRole = Role.ADMIN;
        UserEntity existingUser = UserEntity.builder()
            .id(userId)
            .username("testuser")
            .role(Role.USER)
            .build();

        UserResponseDto expectedDto = new UserResponseDto(userId, "testuser", newRole, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {
            mockedMapper.when(() -> UserMapper.toDto(any(UserEntity.class))).thenReturn(expectedDto);

            UserResponseDto actualDto = userService.changeUserRole(userId, newRole);

            assertNotNull(actualDto);
            assertEquals(newRole, actualDto.role());

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository, times(1)).save(userCaptor.capture());

            UserEntity savedUser = userCaptor.getValue();
            assertEquals(newRole, savedUser.getRole());
            assertEquals(userId, savedUser.getId());
        }
    }

    @Test
    void changeUserRole_UserNotFound_ShouldThrowIllegalArgumentException() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> {
                userService.changeUserRole(userId, Role.ADMIN);
            }
        );

        assertEquals("Unable to find user with id: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void getUsers_ShouldReturnListOfUserDtos() {
        UserEntity user1 = UserEntity.builder().id(1L).username("user1").role(Role.USER).build();
        UserEntity user2 = UserEntity.builder().id(2L).username("user2").role(Role.ADMIN).build();
        List<UserEntity> userList = List.of(user1, user2);

        UserResponseDto dto1 = new UserResponseDto(1L, "user1", Role.USER, null, null);
        UserResponseDto dto2 = new UserResponseDto(2L, "user2", Role.ADMIN, null, null);

        when(userRepository.findAll()).thenReturn(userList);

        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {
            mockedMapper.when(() -> UserMapper.toDto(user1)).thenReturn(dto1);
            mockedMapper.when(() -> UserMapper.toDto(user2)).thenReturn(dto2);

            List<UserResponseDto> result = userService.getUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).username());
            assertEquals(Role.USER, result.get(0).role());
            assertEquals("user2", result.get(1).username());
            assertEquals(Role.ADMIN, result.get(1).role());
        }
    }


    @Test
    void searchUsers_WithFilters_ShouldCallRepositoryWithSpecification() {
        UserSearchRequestFilters filters = new UserSearchRequestFilters(
            "test", List.of(Role.USER), null, null, null, null
        );
        Pageable pageable = PageRequest.of(0, 10);
        SearchRequest<UserSearchRequestFilters> searchRequest = new SearchRequest<>(
            pageable.getPageNumber(), pageable.getPageSize(), filters);

        UserEntity user = UserEntity.builder().id(1L).username("testuser").role(Role.USER).build();
        Page<UserEntity> userPage = new PageImpl<>(List.of(user), pageable, 1);

        UserResponseDto dto = new UserResponseDto(1L, "testuser", Role.USER, null, null);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        try (MockedStatic<UserMapper> mockedMapper = mockStatic(UserMapper.class)) {
            mockedMapper.when(() -> UserMapper.toDto(user)).thenReturn(dto);

            SearchResponseDto<UserResponseDto> response = userService.searchUsers(searchRequest);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals(1, response.getData().size());
            assertEquals("testuser", response.getData().get(0).username());
            assertEquals(Role.USER, response.getData().get(0).role());

            verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void searchUsers_WithoutFilters_ShouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 5);
        SearchRequest<UserSearchRequestFilters> searchRequest = new SearchRequest<>(
            pageable.getPageNumber(), pageable.getPageSize(), null);

        List<UserEntity> users = Collections.emptyList();
        Page<UserEntity> userPage = new PageImpl<>(users, pageable, 0);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        SearchResponseDto<UserResponseDto> response = userService.searchUsers(searchRequest);

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getData().size());

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}