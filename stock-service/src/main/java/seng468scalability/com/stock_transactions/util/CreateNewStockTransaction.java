package seng468scalability.com.stock_transactions.util;

import lombok.RequiredArgsConstructor;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;

@RequiredArgsConstructor
public class CreateNewStockTransaction {

    public final StockTxIdSequenceGenerator generator;
    public StockTransaction createNewStockTx(Long stock_id, boolean is_buy, OrderType orderType, Long quantity, Long price, String username){
        Long stockTxId = generator.getSequenceNumber(StockTransaction.SEQUENCE_NAME);
       return new StockTransaction(stockTxId, stock_id, is_buy, orderType, quantity, price, username);

    }
}
