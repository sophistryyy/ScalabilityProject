package seng468.scalability.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_orders")
public class StockOrder {

    public enum OrderType{
        MARKET,
        LIMIT
    }
    public enum OrderStatus{
        COMPLETED,
        IN_PROGRESS,
        PARTIAL_FULFILLED
    }

    @Id
    @SequenceGenerator(
            name = "stocksOrder_sequence",
            sequenceName = "stocksOrder_sequence",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "stocksOrder_sequence"
    )
    @Column(name = "stock_tx_id")
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;
    private String username;
    private Long parent_stock_tx_id;
    @JsonProperty("stock_id")
    private Long stock_id;
    private boolean is_buy;
    @JsonProperty("order_type")
    private OrderType orderType;
    private Long quantity;
    private Long price;
    private LocalDateTime timestamp;
    private OrderStatus orderStatus;
    private Long walletTXid;
    private Long trueRemainingQuantity;

    private boolean expired;


    public StockOrder() {}

    public StockOrder(Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, String username) {
        this.parent_stock_tx_id = null;
        this.walletTXid = null;
        this.stock_id = stock_id;
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
        this.orderStatus = OrderStatus.IN_PROGRESS;
        this.username = username;
        this.trueRemainingQuantity = quantity;
        this.expired = false;
    }

    public StockOrder(Long parent_stock_tx_id, Long walletTXid, Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, OrderStatus orderStatus, String username) {
        this.parent_stock_tx_id = parent_stock_tx_id;
        this.walletTXid = walletTXid;
        this.stock_id = stock_id;
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = null; // Assign the provided timestamp
        this.orderStatus = orderStatus;
        this.username = username;
        this.trueRemainingQuantity = quantity;
    }

    public StockOrder createCopy(Long newQuantity, OrderStatus orderStatus) {
        StockOrder copy = new StockOrder();
        copy.parent_stock_tx_id = this.parent_stock_tx_id == null ? this.stock_tx_id
                                                                  : this.parent_stock_tx_id; //reference true parent
        copy.stock_id = this.stock_id;
        copy.is_buy = this.is_buy;
        copy.orderType = this.orderType;
        copy.quantity = newQuantity;
        copy.price = this.price;
        copy.timestamp = LocalDateTime.now();
        copy.orderStatus = orderStatus;
        copy.username = this.username;
        copy.walletTXid = null; //set it
        copy.trueRemainingQuantity = 0L;
        expired = false;
        return copy;
    }

    public Long getWalletTXid() {
        return walletTXid;
    }

    public String getUsername() {return username;}

    public Long getStock_tx_id() {return stock_tx_id;}

    public Long getParent_stock_tx_id() {return parent_stock_tx_id;}

    public Long getStockId() {
        return stock_id;
    }

    public boolean getIs_buy() {
        return is_buy;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Long getTrueRemainingQuantity() {
        return trueRemainingQuantity;
    }

    public boolean isExpired() {return expired;}

    public void setTrueRemainingQuantity(Long trueRemainingQuantity) {this.trueRemainingQuantity = trueRemainingQuantity;}

    public void setParent_stock_tx_id(Long parent_stock_tx_id) {this.parent_stock_tx_id = parent_stock_tx_id;}

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setWalletTXid(Long walletTXid) {
        this.walletTXid = walletTXid;
    }

    public void setExpired(boolean expired) {this.expired = expired;}


    @Override
    public String toString() {
        return "StockOrder{" +
                "stock_tx_id=" + stock_tx_id +
                ", parent_stock_tx_id=" + parent_stock_tx_id +
                ", stock_id=" + stock_id +
                ", is_buy=" + is_buy +
                ", orderType=" + (orderType != null ? orderType.toString() : "") + //doesn't compile otherwise
                ", quantity=" + quantity +
                ", price=" + price +
                ", timestamp=" + timestamp +
                ", orderStatus=" + (orderStatus != null ? orderStatus.toString() : "") +
                ", username: " + username +
                "}";
    }

}
