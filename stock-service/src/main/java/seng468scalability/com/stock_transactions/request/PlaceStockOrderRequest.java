package seng468scalability.com.stock_transactions.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlaceStockOrderRequest {

    private Long stock_id;
    private boolean is_buy;
    @JsonProperty("order_type")
    private String orderType;
    private Long quantity;
    private Long price;

}
