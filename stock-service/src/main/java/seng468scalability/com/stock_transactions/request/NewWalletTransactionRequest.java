package seng468scalability.com.stock_transactions.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewWalletTransactionRequest {
    private String username;
    private Long stockTXId;
    private boolean isDebit;
    private Long amount;
}
