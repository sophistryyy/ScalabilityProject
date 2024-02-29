package seng468.scalability.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
        PARTIAL_FULFILLED,
        EXPIRED,
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
    private Integer stock_tx_id;

    private String username;
    private Integer parent_stock_tx_id;
    @JsonProperty("stock_id")
    private int stock_id;

    private boolean is_buy;
    @JsonProperty("order_type")
    private OrderType orderType;
    private Integer quantity;
    private Integer price;
    private LocalDateTime timestamp;
    private OrderStatus orderStatus;

    private Integer walletTXid;
    private Integer trueRemainingQuantity;


    public StockOrder() {}

    public StockOrder(int stock_id, boolean is_buy, OrderType orderType, Integer quantity, Integer price, String username) {
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
    }

    public StockOrder(Integer parent_stock_tx_id, Integer walletTXid, int stock_id, boolean is_buy, OrderType orderType, Integer quantity, Integer price, OrderStatus orderStatus, String username) {
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

    public StockOrder createCopy(Integer newQuantity, OrderStatus orderStatus) {
        //if orderStatus is null then it's a OrderStatus.IN_PROGRESS_FROM_PARTIAL
        StockOrder copy = new StockOrder();
        copy.parent_stock_tx_id = this.parent_stock_tx_id == null ? this.stock_tx_id : this.parent_stock_tx_id;
        copy.stock_id = this.stock_id;
        copy.is_buy = this.is_buy;
        copy.orderType = this.orderType;
        copy.quantity = newQuantity;
        copy.price = this.price;
        copy.timestamp = LocalDateTime.now();//original timestamp
        copy.orderStatus = orderStatus;
        copy.username = this.username;
        copy.walletTXid = null; //for now
        //add wallet
        return copy;
    }

    public Integer getWalletTXid() {
        return walletTXid;
    }

    public String getUsername() {return username;}

    public Integer getStock_tx_id() {return stock_tx_id;}

    public Integer getParent_stock_tx_id() {return parent_stock_tx_id;}

    public int getStockId() {
        return stock_id;
    }

    public boolean getIs_buy() {
        return is_buy;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Integer getTrueRemainingQuantity() {
        return trueRemainingQuantity;
    }

    public void setTrueRemainingQuantity(Integer trueRemainingQuantity) {
        this.trueRemainingQuantity = trueRemainingQuantity;
    }

    public void setParent_stock_tx_id(Integer parent_stock_tx_id) {this.parent_stock_tx_id = parent_stock_tx_id;}

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setStock_tx_id(Integer stock_tx_id) {
        this.stock_tx_id = stock_tx_id;
    }

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
                ", TEST=username " + username +
                "}";
    }

}
