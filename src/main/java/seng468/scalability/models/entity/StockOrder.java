package seng468.scalability.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;

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
    private Integer transaction_id;

    @JsonProperty("stock_id")
    private int stock_id;
    @JsonProperty("is_buy")
    private boolean is_buy;
    @JsonProperty("order_type")
    private OrderType orderType;
    private Integer quantity;
    private Integer price;
    private LocalDate timestamp;
    private OrderStatus orderStatus;



    public StockOrder() {}

    public StockOrder(int stock_id, boolean is_buy, OrderType orderType, Integer quantity, Integer price) {

        this.stock_id = stock_id;
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDate.now();
        this.orderStatus = OrderStatus.IN_PROGRESS;
    }

    /*
    public StockOrder(Integer transaction_id, Stock stock, boolean is_buy,
                      OrderType orderType, Integer quantity, Integer price, LocalDate timestamp, OrderStatus orderStatus) {
        this.transaction_id = transaction_id;
        this.stock = stock;
        this.stock_id = stock.getId();
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;
    }*/

    public Integer getTransaction_id() {
        return transaction_id;
    }

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

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }



    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "StockOrder{" +
                "transaction_id=" + transaction_id +
                ", stock_id=" + stock_id +
                ", is_buy=" + is_buy +
                ", orderType=" + (orderType != null ? orderType.toString() : null) +
                ", quantity=" + quantity +
                ", price=" + price +
                ", timestamp=" + timestamp +
                ", orderStatus=" + (orderStatus != null ? orderStatus.toString() : null) +
                '}';
    }

}
