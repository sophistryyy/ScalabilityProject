package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddMoneyToWalletRequest {
    Integer amount;

    public AddMoneyToWalletRequest(@JsonProperty("amount") Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return this.amount;
    }
}
