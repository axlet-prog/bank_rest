package com.example.bankcards.service;

import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    SearchResponseDto<UserResponseDto> searchUsers(SearchRequest<UserSearchRequestFilters> searchRequest);

    List<UserResponseDto> getUsers();

    UserResponseDto changeUserRole(Long userId, Role newRole);
}
