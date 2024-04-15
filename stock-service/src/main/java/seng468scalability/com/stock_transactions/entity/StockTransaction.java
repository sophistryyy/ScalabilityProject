package seng468scalability.com.stock_transactions.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "stock_transactions")
public class StockTransaction {

    public static final String SEQUENCE_NAME = "stock_tx_id_sequence";
    @Id
    private Long stock_tx_id;


    @JsonProperty("stock_id")
    private Long stockId;
    @JsonProperty("is_buy")
    private boolean is_buy;
    @JsonProperty("order_type")
    private OrderType orderType;
    private Long quantity;
    private Long price;

    private String username;
    private Long parent_stock_tx_id;


    @JsonProperty("wallet_tx_id")
    private Long walletTXid;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;
    private LocalDateTime timestamp;
    private Long trueRemainingQuantity;
    @JsonProperty("expired")
    private boolean expired;



    public StockTransaction(Long stock_tx_id, Long parentStockTXId, Long walletTXId, Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, OrderStatus orderStatus, String username) {
        this.stock_tx_id = stock_tx_id;
        this.parent_stock_tx_id = parentStockTXId;
        this.walletTXid = walletTXId;
        this.stockId = stock_id;
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
        this.orderStatus = orderStatus;
        this.username = username;
        this.trueRemainingQuantity = quantity;
        this.expired = false;
    }




}
