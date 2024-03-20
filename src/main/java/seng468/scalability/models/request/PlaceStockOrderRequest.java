package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import seng468.scalability.models.entity.StockOrder;

public class PlaceStockOrderRequest {

    private Long stock_id;
    private boolean is_buy;
    @JsonProperty("order_type")
    private String orderType;
    private Long quantity;
    private Long price;
    public PlaceStockOrderRequest()
    {}

    public Long getStock_id() {
        return stock_id;
    }

    public boolean getIs_buy() {
        return is_buy;
    }

    public String getOrderType() {
        return orderType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getPrice() {
        return price;
    }
}
