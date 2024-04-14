package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalUpdateUserStockRequest(Long stockId, Long quantity, String username, boolean add) {
    public InternalUpdateUserStockRequest(@JsonProperty("stock_id") Long stockId, Long quantity, String username, boolean add) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.username = username;
        this.add = add;
    }

}
