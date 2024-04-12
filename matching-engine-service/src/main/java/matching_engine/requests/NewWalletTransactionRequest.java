package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewWalletTransactionRequest implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("isDebit")
    private boolean isDebit;
    @JsonProperty("amount")
    private Long amount;
}
