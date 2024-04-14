package seng468scalability.com.stock_transactions.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;
import seng468scalability.com.stock_transactions.request.InternalDeleteStockTXRequest;

@RequiredArgsConstructor
@RestController
public class InternalDeleteStockTransaction {

    @Autowired
    StockTransactionsRepository stockTransactionsRepository;

    @PostMapping("/internal/deleteStockTransaction")
    public Response deleteStockTX(@RequestBody InternalDeleteStockTXRequest req) {
        try { 
            stockTransactionsRepository.deleteById(req.stockTXId());
            return Response.ok("Success");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }   
    }
}
