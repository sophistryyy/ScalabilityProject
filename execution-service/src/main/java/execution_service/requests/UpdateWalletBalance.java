package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletBalance {
    private String username;
    private Long amount;
    @JsonProperty("is_debit")
    private Boolean isDebit;
}
