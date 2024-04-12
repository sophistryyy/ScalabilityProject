package execution_service.utils;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;

public class OrderExecutionService {
    public void execute(OrderExecutionMessage orderExecutionMessage) {
        StockTransaction buyerTransaction = orderExecutionMessage.getBuyStockTransaction();
        StockTransaction sellerTransaction = orderExecutionMessage.getSellStockTransaction();

        
    }
}
