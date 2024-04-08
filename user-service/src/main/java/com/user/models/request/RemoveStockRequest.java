package com.user.models.request;

public record RemoveStockRequest (String username, Long stock_id, Long amount) {
}
