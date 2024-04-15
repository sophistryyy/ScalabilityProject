package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewWalletTransactionRequest implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("is_debit")
    private boolean isDebit;
    @JsonProperty("amount")
    private Long amount;
}
