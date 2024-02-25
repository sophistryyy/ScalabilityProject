package seng468.scalability.models.request__;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddStockToUserRequest {
    private int stockId;
    private int quantity;

    public AddStockToUserRequest(@JsonProperty("stock_id") int stockId, int quantity) {
        this.stockId = stockId;
        this.quantity = quantity;
    }

    public int getStockId() {
        return this.stockId;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
