package seng468scalability.com.stock_transactions.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.entity.NewStockTransactionRequest;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;
import seng468scalability.com.stock_transactions.util.StockOrderUtil;

@RequiredArgsConstructor
@RestController
public class InternalCreateStockTransaction {
  
    private final StockOrderUtil stockUtil;

    @Autowired
    private StockTransactionsRepository stockTransactionsRepository;    


    @PostMapping("/internal/createStockTransaction")
    public Response createStockTransaction(@RequestBody NewStockTransactionRequest req) {
        try {
            StockTransaction stockTransaction = stockUtil.createStockTX(req.getStockTXId(), req.getStockId(), req.getParentStockTXId(), req.getWalletTXid(),
                                                        req.isBuy(), req.getOrderType(), req.getQuantity(), req.getPrice(), req.getOrderStatus(), req.getUsername());
            System.out.println(stockTransaction.getOrderStatus());
            System.out.println(stockTransaction.getWalletTXid());
            stockTransactionsRepository.save(stockTransaction);

            return Response.ok(stockTransaction.getStock_tx_id().toString());
        } catch (Exception e) {
        return Response.error(e.getMessage());
        }
    }
}
