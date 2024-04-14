package com.wallet.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalDeleteWalletTXRequest(@JsonProperty("wallet_tx_id") Long walletTXId) {}
