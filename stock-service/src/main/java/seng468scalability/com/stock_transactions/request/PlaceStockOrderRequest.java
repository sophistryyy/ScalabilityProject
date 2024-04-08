package seng468scalability.com.stock_transactions.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


public record PlaceStockOrderRequest(Long stock_id, @JsonProperty("is_buy")  Boolean is_buy,
                                     @JsonProperty("order_type") String orderType, Long quantity,
                                     Long price) {


}
