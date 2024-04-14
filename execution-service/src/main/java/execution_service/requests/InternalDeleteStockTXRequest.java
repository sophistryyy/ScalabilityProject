package execution_service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalDeleteStockTXRequest(@JsonProperty("stock_tx_id") Integer stockTXId) {}
