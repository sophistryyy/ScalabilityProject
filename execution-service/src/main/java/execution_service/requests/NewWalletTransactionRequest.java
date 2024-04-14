package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewWalletTransactionRequest {
    @JsonProperty("wallet_tx_id")
    private Long walletTXId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("stock_tx_id")
    private Long stockTXId;
    @JsonProperty("is_debit")
    private boolean isDebit;
    @JsonProperty("amount")
    private Long amount;

    public void setWalletTXId(Long walletTXId) {
        this.walletTXId = walletTXId;
    }

    public void setStockTXId(Long stockTXId) {
        this.stockTXId = stockTXId;
    }
}
