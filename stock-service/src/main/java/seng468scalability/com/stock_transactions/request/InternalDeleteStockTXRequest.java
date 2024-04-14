package seng468scalability.com.stock_transactions.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalDeleteStockTXRequest(@JsonProperty("stock_tx_id") Integer stockTXId) {}
