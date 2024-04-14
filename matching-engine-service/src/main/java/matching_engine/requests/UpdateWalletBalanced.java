package matching_engine.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletBalanced {
    @JsonProperty("username")
    private String username;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("is_debit")
    private Boolean isDebit;
}
