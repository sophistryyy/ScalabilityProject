package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class UpdateUserStockRequest implements Serializable {
    @JsonProperty("stock_id")
    private Long stockId;
    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("username")
    private String username;
    @JsonProperty("add")
    private boolean add;


}
