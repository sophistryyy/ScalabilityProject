package seng468scalability.com.stock_transactions.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalDeleteWalletTXRequest(@JsonProperty("wallet_tx_id") Long walletTXId) {}
