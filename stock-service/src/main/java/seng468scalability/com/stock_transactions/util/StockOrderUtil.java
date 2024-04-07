package seng468scalability.com.stock_transactions.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;
import seng468scalability.com.stock_transactions.request.PlaceStockOrderRequest;

@RequiredArgsConstructor
@Component
public class StockOrderUtil {

    public final StockTxIdSequenceGenerator generator;
    public StockTransaction createNewStockTx(Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, String username){
        Long stockTxId = generator.getSequenceNumber(StockTransaction.SEQUENCE_NAME);
       return new StockTransaction(stockTxId, stock_id, is_buy, orderType, quantity, price, username);

    }

    public String basicVerifier(PlaceStockOrderRequest req)
    {
        OrderType orderType;
        try {
            orderType = OrderType.valueOf(req.getOrderType());
        } catch (Exception e) {
            return "Incorrect value of order type";
        }
        if (orderType == OrderType.MARKET && req.getPrice() != null) {
            return "MARKET orders can't have price, set it to null.";
        }
        if (orderType == OrderType.LIMIT && (req.getPrice() == null || req.getPrice() <= 0)) {
            return "LIMIT orders' price has to be more than 0";
        }
        if(req.getQuantity() == null || req.getQuantity() <= 0){
            return "Please set quantity to more than 0";
        }
        if(req.getStock_id() == null){
            return "Null stock id is not allowed";
        }
        return null;
    }


}
