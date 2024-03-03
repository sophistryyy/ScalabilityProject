package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import seng468.scalability.models.entity.StockOrder;

public class PlaceStockOrderRequest {

    private int stock_id;
    private boolean is_buy;
    @JsonProperty("order_type")
    private String orderType;
    private Integer quantity;
    private Integer price;
    public PlaceStockOrderRequest()
    {}

    public int getStock_id() {
        return stock_id;
    }

    public boolean getIs_buy() {
        return is_buy;
    }

    public String getOrderType() {
        return orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }
}
