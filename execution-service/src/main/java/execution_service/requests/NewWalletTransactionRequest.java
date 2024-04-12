package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewWalletTransactionRequest implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("isDebit")
    private boolean isDebit;
    @JsonProperty("amount")
    private Long amount;
}
