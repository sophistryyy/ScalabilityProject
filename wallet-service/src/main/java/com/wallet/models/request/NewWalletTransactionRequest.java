package com.wallet.models.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NewWalletTransactionRequest {
    private String username;
    private Long stockTXId;
    private boolean isDebit;
    private Long amount;
}
