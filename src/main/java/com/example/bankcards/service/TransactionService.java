package com.example.bankcards.service;

import com.example.bankcards.dto.transaction.TransactionRequest;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    void makeTransaction(TransactionRequest request);

}
