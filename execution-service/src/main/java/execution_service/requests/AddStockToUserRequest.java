package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStockToUserRequest implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("stock_id")
    private Long stockId;
    @JsonProperty("quantity")
    private Long quantity;


}
