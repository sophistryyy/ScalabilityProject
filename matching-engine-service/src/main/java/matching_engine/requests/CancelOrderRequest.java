package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matching_engine.entity.enums.OrderType;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class CancelOrderRequest implements Serializable {
    @JsonProperty("stock_tx_id")
    private Long stock_tx_id;

    @JsonProperty("order_type")
    private OrderType orderType;

    @JsonProperty("is_buy")
    private Boolean isBuy;

    @JsonProperty("stock_id")
    private Long stock_id;

}
