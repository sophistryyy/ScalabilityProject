package execution_service.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletTransactionRequest {
    private String username;
    private Long stockTXId;
    private boolean isDebit;
    private Long amount;
}
