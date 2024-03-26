package seng468.scalability.com.stock.models.entity;

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
