package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import execution_service.entity.enums.OrderStatus;
import execution_service.entity.enums.OrderType;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewStockTransactionRequest implements Serializable  {
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;
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
    private Long parent_stock_tx_id;
    @JsonProperty("wallet_tx_id")
    private Long walletTXId;

    public void setWalletTxId(Long walletTXId) {
        this.walletTXId = walletTXId;
    }

    public void setStockTXId(Long stockTXId) {
        this.stock_tx_id = stockTXId;
    }
}
