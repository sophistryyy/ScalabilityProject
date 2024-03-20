package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddStockToUserRequest {
    private Long stockId;
    private int quantity;

    public AddStockToUserRequest(@JsonProperty("stock_id") Long stockId, int quantity) {
        this.stockId = stockId;
        this.quantity = quantity;
    }

    public Long getStockId() {
        return this.stockId;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
