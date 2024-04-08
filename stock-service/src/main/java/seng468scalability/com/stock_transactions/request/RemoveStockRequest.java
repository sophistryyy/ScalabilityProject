package seng468scalability.com.stock_transactions.request;


public record RemoveStockRequest(String username, Long stock_id, Long amount) {
}
