package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matching_engine.entity.enums.OrderType;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewStockTransactionRequest implements Serializable  {
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;
    @JsonProperty("stock_id")
    private Long stockId; 
    @JsonProperty("is_buy")
    private boolean isBuy;
    @JsonProperty("order_type")
    private OrderType orderType;
    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("price")
    private Long price;
    @JsonProperty("username")
    private String username;
    @JsonProperty("parent_stock_tx_id")
    private Long parent_stock_tx_id;
    @JsonProperty("walletTXid")
    private Long walletTXid;
}
