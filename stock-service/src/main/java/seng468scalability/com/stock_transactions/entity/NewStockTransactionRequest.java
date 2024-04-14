package seng468scalability.com.stock_transactions.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewStockTransactionRequest implements Serializable  {
    @JsonProperty("stock_tx_id")
    private Long stockTXId;
    @JsonProperty("stock_id")
    private Long stockId; 
    @JsonProperty("is_buy")
    private boolean isBuy;
    @JsonProperty("order_status")
    private OrderStatus orderStatus;
    @JsonProperty("order_type")
    private OrderType orderType;
    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("price")
    private Long price;
    @JsonProperty("username")
    private String username;
    @JsonProperty("parent_stock_tx_id")
    private Long parentStockTXId;
    @JsonProperty("wallet_tx_id")
    private Long walletTXid;
}
