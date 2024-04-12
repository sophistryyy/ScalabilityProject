package execution_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderExecutionMessage {
    private StockTransaction buyStockTransaction;
    private StockTransaction sellStockTransaction;
}
