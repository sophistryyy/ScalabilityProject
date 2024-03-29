package seng468scalability.com.stock.entity;

public class enums {
    public enum OrderType{
        MARKET,
        LIMIT
    }
    public enum OrderStatus{
        COMPLETED,
        IN_PROGRESS,
        PARTIAL_FULFILLED
    }
}
