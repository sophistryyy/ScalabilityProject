package seng468.scalability.models.Entity;

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
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @Transient
    private Integer stock_id;

    private boolean is_buy;
    private OrderType orderType;
    private Integer quantity;
    private Integer price;
    private LocalDate timestamp;
    private OrderStatus orderStatus;



    public StockOrder() {}

    public StockOrder(Stock stock, boolean is_buy, OrderType orderType, Integer quantity, Integer price) {
        this.stock = stock;
        this.stock_id = stock.getId();
        this.is_buy = is_buy;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDate.now();
        this.orderStatus = OrderStatus.IN_PROGRESS;
    }

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
    }

    public Integer getTransaction_id() {
        return transaction_id;
    }

    public Integer getStockId() {
        return stock_id;
    }

    public boolean isIs_buy() {
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
                ", orderType=" + orderType.toString() +
                ", quantity=" + quantity +
                ", price=" + price +
                ", timestamp=" + timestamp +
                ", orderStatus=" + orderStatus.toString() +
                '}';
    }
}
