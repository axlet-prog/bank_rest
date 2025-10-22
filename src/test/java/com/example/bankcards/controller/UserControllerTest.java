package com.example.bankcards.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UserSearchRequestFilters;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserServiceDefault;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceDefault userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @WithMockUser(authorities = { "ADMIN" })
    void getUser_ReturnsUser_WhenFound() throws Exception {
        UserSearchRequestFilters filters = new UserSearchRequestFilters(
            "test",
            List.of(Role.USER),
            LocalDateTime.now(),
            null,
            null,
            null
        );
        SearchRequest<UserSearchRequestFilters> searchRequest = new SearchRequest<>(
            0,
            10,
            filters
        );

        UserResponseDto userDto = new UserResponseDto(
            1L,
            "testuser",
            Role.USER,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        SearchResponseDto<UserResponseDto> responseDto = SearchResponseDto.of(
            List.of(userDto), 1L, searchRequest.getPageable()
        );

        when(userService.searchUsers(any(SearchRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users/search")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.data[0].id").value(1L))
            .andExpect(jsonPath("$.data[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = { "USER" })
    void getUser_ReturnsException_WhenUserRole() throws Exception {
        SearchRequest<UserSearchRequestFilters> searchRequest = new SearchRequest<>(
            0,
            1,
            null
        );
        when(userService.searchUsers(any(SearchRequest.class))).thenReturn(null);
        mockMvc.perform(post("/users/search")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void getUser_ReturnsException_WhenUnauthorized() throws Exception {
        SearchRequest<UserSearchRequestFilters> searchRequest = new SearchRequest<>(
            0,
            1,
            null
        );
        when(userService.searchUsers(any(SearchRequest.class))).thenReturn(null);
        mockMvc.perform(post("/users/search")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = { "ADMIN" })
    void changeUserTestSucceed() throws Exception {

        when(userService.changeUserRole(1L, Role.ADMIN)).thenReturn(
            new UserResponseDto(1L, "testuser", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now())
        );

        mockMvc.perform(post("/users/1/change_role")
                            .param("newRole", "ADMIN")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN" })
    void getUsersTestSucceed() throws Exception {

        when(userService.getUsers()).thenReturn(
            List.of(
                new UserResponseDto(1L, "user1", Role.USER, LocalDateTime.now(), LocalDateTime.now()),
                new UserResponseDto(2L, "user2", Role.USER, LocalDateTime.now(), LocalDateTime.now())
            )
        );

        mockMvc.perform(get("/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].role").value(Role.USER.name()))
            .andExpect(jsonPath("$.[0].username").value("user1"))
            .andExpect(jsonPath("$.[1].username").value("user2"));
    }
}



