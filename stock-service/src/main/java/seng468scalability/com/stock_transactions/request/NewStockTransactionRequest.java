package seng468scalability.com.stock_transactions.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewStockTransactionRequest {
    @JsonProperty("stock_id")
    private Long stockId; 
    @JsonProperty("is_buy")
    private boolean isBuy; 
    @JsonProperty("order_type")
    private OrderType orderType; 
    private Long quantity;
    private Long price;
    private String username;
}
