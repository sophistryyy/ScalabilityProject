package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelOrderRequest {
    @JsonProperty("stock_tx_id")
    private Long transactionId;

    public CancelOrderRequest() {

    }

    public Long getTransactionId() {
        return transactionId;
    }
}
