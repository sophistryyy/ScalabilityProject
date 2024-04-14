package seng468scalability.com.portfolio.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record InternalAddStockToUserRequest(Long stockId, Long quantity, String username) {
    public InternalAddStockToUserRequest(@JsonProperty("stock_id") Long stockId, Long quantity, String username) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.username = username;
    }

}
