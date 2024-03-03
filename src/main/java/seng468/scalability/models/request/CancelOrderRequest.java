package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelOrderRequest {
    @JsonProperty("stock_tx_id")
    private Integer transactionId;

    public CancelOrderRequest() {

    }

    public Integer getTransactionId() {
        return transactionId;
    }
}
