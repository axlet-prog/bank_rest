package com.example.bankcards.controller;

import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Управление пользователями", description = "Эндпоинты для поиска пользователей и управления их ролями")
public class UserController {

    private final UserService userService;

    @Operation(description = "Получение списка пользователей с использованием фильтров и пагинации.")
    @GetMapping("/")
    public ResponseEntity<SearchResponseDto<UserResponseDto>> getUsers(
        @RequestBody SearchRequest<UserSearchRequestFilters> request
    ) {
        return ResponseEntity.ok(userService.getUsers(request));
    }

    @Operation(description = "Изменение роли пользователя по его ID.")
    @PostMapping("/{userId}/change_role")
    public ResponseEntity<UserResponseDto> changeUserRole(
        @Parameter(
            description = "Идентификатор пользователя, чья роль изменяется",
            required = true)
        @PathVariable Long userId,

        @Parameter(description = "Новая роль для пользователя",
                   required = true, example = "ROLE_ADMIN")
        @RequestParam Role newRole
    ) {
        return ResponseEntity.ok(userService.changeUserRole(userId, newRole));
    }
}
