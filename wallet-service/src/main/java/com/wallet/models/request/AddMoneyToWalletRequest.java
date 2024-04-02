package com.wallet.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class AddMoneyToWalletRequest {
    Long amount;

    public AddMoneyToWalletRequest(@JsonProperty("amount") Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return this.amount;
    }
}
