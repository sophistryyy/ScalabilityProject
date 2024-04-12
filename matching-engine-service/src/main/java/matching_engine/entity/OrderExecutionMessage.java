package matching_engine.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderExecutionMessage {
    private StockTransaction buyStockTransaction;
    private StockTransaction sellStockTransaction;
    private boolean expired;
}
