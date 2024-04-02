package seng468scalability.com.stock_transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import seng468scalability.com.stock.entity.enums;

import java.time.LocalDateTime;

import static seng468scalability.com.stock.entity.enums.OrderStatus;
import static seng468scalability.com.stock.entity.enums.OrderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_orders")
public class StockOrder {
    @Id
    @SequenceGenerator(
            name = "stocksOrder_sequence",
            sequenceName = "stocksOrder_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "stocksOrder_sequence"
    )
    @Column(name = "stock_tx_id")
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;
    @JsonProperty("stock_id")
    private Long stock_id;
    @JsonProperty("order_type")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String username;
    private Long parent_stock_tx_id;
    private boolean is_buy;
    private Long quantity;
    private Long price;
    private LocalDateTime timestamp;
    private Long walletTXid;
    private Long trueRemainingQuantity;
    private boolean expired;



    public StockOrder(Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, String username) {
        this.parent_stock_tx_id = null;
        this.walletTXid = null;
        this.stock_id = stock_id;
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
        this.orderStatus = enums.OrderStatus.IN_PROGRESS;
        this.username = username;
        this.trueRemainingQuantity = quantity;
        this.expired = false;
    }


    public StockOrder createCopy(Long newQuantity, OrderStatus newOrderStatus) {
        StockOrder copy = StockOrder.builder().stock_id(this.stock_id).is_buy(this.is_buy).orderType(this.orderType).quantity(newQuantity)
                .price(this.price).timestamp(LocalDateTime.now()).orderStatus(newOrderStatus).username(this.username).walletTXid(null)
                .expired(false).trueRemainingQuantity(0L).build();
        copy.parent_stock_tx_id = this.parent_stock_tx_id == null ? this.stock_tx_id
                                                                  : this.parent_stock_tx_id; //reference true parent
        return copy;
    }


}
