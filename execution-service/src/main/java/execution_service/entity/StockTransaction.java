package execution_service.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import execution_service.entity.enums.OrderStatus;
import execution_service.entity.enums.OrderType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction implements Serializable{
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;

    @JsonProperty("stock_id")
    private Long stock_id;

    @JsonProperty("is_buy")
    private Boolean is_buy;

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

    @JsonProperty("orderStatus")
    private OrderStatus orderStatus;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("trueRemainingQuantity")
    private Long trueRemainingQuantity;

    @JsonProperty("expired")
    private boolean expired;

}
