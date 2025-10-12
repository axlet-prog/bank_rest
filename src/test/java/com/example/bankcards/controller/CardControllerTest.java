package com.example.bankcards.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchRequestFilters;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.PatchCardRequest;
import com.example.bankcards.dto.search.SearchRequest;
import com.example.bankcards.dto.search.SearchResponseDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardServiceDefault;
import com.example.bankcards.service.UserServiceDefault;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardServiceDefault cardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserServiceDefault userService;

    @Test
    @WithMockUser
    void searchCards_ShouldReturnCards() throws Exception {
        CardSearchRequestFilters filters = new CardSearchRequestFilters();
        SearchRequest<CardSearchRequestFilters> searchRequest = new SearchRequest<>(0, 10, filters);

        CardResponseDto cardDto = new CardResponseDto(
            1L, 1L, "************1234",
            LocalDate.now().plusYears(3), CardStatus.ACTIVE, 1000.0,
            LocalDateTime.now(), LocalDateTime.now()
        );
        SearchResponseDto<CardResponseDto> responseDto = SearchResponseDto.of(
            Collections.singletonList(cardDto), 1L, searchRequest.getPageable()
        );

        when(cardService.searchCards(any(SearchRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/cards/search")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.data[0].cardId").value(1L))
            .andExpect(jsonPath("$.data[0].cardNumber").value("************1234"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void getCards_AsAdmin_ShouldReturnAllCards() throws Exception {
        List<CardResponseDto> cards = List.of(
            new CardResponseDto(
                1L, 1L, "************1111", LocalDate.now().plusYears(1), CardStatus.ACTIVE, 1500.0,
                LocalDateTime.now(), LocalDateTime.now()
            ),
            new CardResponseDto(
                2L, 2L, "************2222", LocalDate.now().plusYears(2), CardStatus.BLOCKED, 2500.0,
                LocalDateTime.now(), LocalDateTime.now()
            )
        );

        when(cardService.getCards()).thenReturn(cards);

        mockMvc.perform(get("/cards")
                            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].cardNumber").value("************1111"))
            .andExpect(jsonPath("$[1].cardNumber").value("************2222"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void createCard_AsAdmin_ShouldCreateCard() throws Exception {
        CreateCardRequest createRequest = new CreateCardRequest(1L);
        CardResponseDto createdCard = new CardResponseDto(
            1L, 1L, "************5678",
            LocalDate.now().plusYears(5), CardStatus.ACTIVE, 0.0,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(createdCard);

        mockMvc.perform(post("/cards")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cardId").value(1L))
            .andExpect(jsonPath("$.ownerId").value(1L))
            .andExpect(jsonPath("$.cardNumber").value("************5678"));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void createCard_AsUser_ShouldBeForbidden() throws Exception {
        CreateCardRequest createRequest = new CreateCardRequest(1L);

        mockMvc.perform(post("/cards")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = { "ADMIN" })
    void changeCard_AsAdmin_ShouldUpdateCard() throws Exception {
        PatchCardRequest patchRequest = new PatchCardRequest(CardStatus.BLOCKED, new BigDecimal("500.25"));
        CardResponseDto updatedCard = new CardResponseDto(
            1L, 1L, "************1234",
            LocalDate.now().plusYears(3), CardStatus.BLOCKED, 500.25,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(cardService.updateCard(eq(1L), any(PatchCardRequest.class))).thenReturn(updatedCard);

        mockMvc.perform(patch("/cards/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cardId").value(1L))
            .andExpect(jsonPath("$.cardStatus").value("BLOCKED"))
            .andExpect(jsonPath("$.balance").value(500.25));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void changeCard_AsUser_ShouldBeForbidden() throws Exception {
        PatchCardRequest patchRequest = new PatchCardRequest(CardStatus.BLOCKED, null);

        mockMvc.perform(patch("/cards/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void deleteCard_AsAdmin_ShouldDeleteCard() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/cards/{id}", 1L)
                            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void deleteCard_AsUser_ShouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/cards/{id}", 1L)
                            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void getBalance_AsUser_ShouldReturnTotalBalance() throws Exception {
        BigDecimal totalBalance = new BigDecimal("12345.67");
        when(cardService.getTotalBalance()).thenReturn(totalBalance);

        mockMvc.perform(get("/cards/balance")
                            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("12345.67"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void getBalance_AsAdmin_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/cards/balance")
                            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    void getBalance_Unauthorized_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/cards/balance")
                            .with(csrf()))
            .andExpect(status().isForbidden());
    }
}
