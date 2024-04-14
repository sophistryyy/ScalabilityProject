package matching_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import matching_engine.requests.InternalUpdateUserStockRequest;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@ToString
public class OrderExecutionMessage implements Serializable {
    @JsonProperty("stock_tx_request")
    private NewStockTransactionRequest newStockTransaction;
    @JsonProperty("wallet_tx_request")
    private NewWalletTransactionRequest newWalletTransaction;
    @JsonProperty("add_stock_to_user_request")
    private InternalUpdateUserStockRequest updateUserStockRequest;
    @JsonProperty("expired")
    private boolean expired;
}
