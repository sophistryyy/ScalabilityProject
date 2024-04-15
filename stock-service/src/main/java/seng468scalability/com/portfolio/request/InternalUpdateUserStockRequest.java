package seng468scalability.com.portfolio.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public record InternalUpdateUserStockRequest(Long stockId, Long quantity, String username, boolean add) implements Serializable {

    public InternalUpdateUserStockRequest(@JsonProperty("stock_id") Long stockId,
                                          @JsonProperty("quantity") Long quantity,
                                          @JsonProperty("username") String username,
                                          @JsonProperty("add") boolean add) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.username = username;
        this.add = add;
    }

}
