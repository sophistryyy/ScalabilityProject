package seng468scalability.com.stock_transactions.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;
import seng468scalability.com.stock_transactions.request.NewStockTransactionRequest;
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
            // Receive timestamp in order execution or create in endpoint??
            StockTransaction stockTransaction = stockUtil.createNewStockTx(req.getStockId(), req.isBuy(), req.getOrderType(),
                                                     req.getQuantity(), req.getPrice(), req.getUsername());
            stockTransactionsRepository.save(stockTransaction);

            return Response.ok(stockTransaction.getStock_tx_id());
        } catch (Exception e) {
        return Response.error(e.getMessage());
        }
    }
}
