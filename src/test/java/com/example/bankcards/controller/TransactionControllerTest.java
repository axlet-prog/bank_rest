package com.example.bankcards.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.exception.ApiControllerAdvice;
import com.example.bankcards.exception.BusinessLogicException;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({ SecurityConfig.class, ApiControllerAdvice.class })
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_AsUser_ShouldSucceed() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("100.00"));
        doNothing().when(transactionService).makeTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void makeTransaction_AsUnauthorized_ShouldBeForbidden() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("100.00"));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void makeTransaction_AsAdmin_ShouldBeForbidden() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("100.00"));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_WithInvalidBody_ShouldReturnBadRequest() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest(1L, 2L, new BigDecimal("-50.00"));
        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_WhenCardNotFound_ShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(99L, 2L, new BigDecimal("100.00"));
        String errorMessage = "Unable to find card with id: 99";

        doThrow(new IllegalArgumentException(errorMessage))
            .when(transactionService).makeTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_WhenCardNotOwnedByUser_ShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("100.00"));
        String errorMessage = "User cards don't match";

        doThrow(new IllegalArgumentException(errorMessage))
            .when(transactionService).makeTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_WhenInsufficientFunds_ShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("10000.00"));
        String errorMessage = "There are not enough money in the balance of the card";

        doThrow(new BusinessLogicException(errorMessage))
            .when(transactionService).makeTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void makeTransaction_WhenCardIsNotActive_ShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("100.00"));
        String errorMessage = "Cards must be ACTIVE";

        doThrow(new BusinessLogicException(errorMessage))
            .when(transactionService).makeTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage));
    }
}