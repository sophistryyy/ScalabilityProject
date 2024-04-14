package seng468scalability.com.portfolio.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record AddStockToUserRequest(Long stockId, Long quantity) {
    public AddStockToUserRequest(@JsonProperty("stock_id") Long stockId, Long quantity) {
        this.stockId = stockId;
        this.quantity = quantity;
    }

}