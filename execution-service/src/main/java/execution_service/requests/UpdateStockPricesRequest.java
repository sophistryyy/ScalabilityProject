package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UpdateStockPricesRequest implements Serializable {
    @JsonProperty("stock_id")
    Long stockId;
    @JsonProperty("price")
    Long price;
}
