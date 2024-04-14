package matching_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletBalanced {
    private String username;
    private Long amount;
    @JsonProperty("is_debit")
    private Boolean isDebit;
}