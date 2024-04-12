package matching_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class StockTransaction implements Serializable{
    @Id
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
