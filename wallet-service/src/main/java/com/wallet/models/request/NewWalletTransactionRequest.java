package com.wallet.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewWalletTransactionRequest {
    @JsonProperty("wallet_tx_id")
    private Long walletTXId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("stock_tx_id")
    private Long stockTXId;
    @JsonProperty("is_debit")
    private boolean isDebit;
    @JsonProperty("amount")
    private Long amount;
}
