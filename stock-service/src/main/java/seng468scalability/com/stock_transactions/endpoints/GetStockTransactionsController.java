package seng468scalability.com.stock_transactions.endpoints;



import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class GetStockTransactionsController {
    private final StockTransactionsRepository stockTransactionsRepository;

    @GetMapping(path = "/getStockTransactions")
    public Response getStockTransactions(@RequestHeader("X-Username") String username){
        try{
            List<StockTransaction> transLst = stockTransactionsRepository.findAllByUsername(username);
            List<Map<String, Object>> data = formatData(transLst);
            System.out.println(data);
            return Response.ok(data);
        }catch(Exception e)
        {
            return Response.error(e.getMessage());
        }
    }

    private List<Map<String, Object>> formatData(List<StockTransaction> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (StockTransaction entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_tx_id", entry.getStock_tx_id());
            entryMap.put("parent_stock_tx_id", entry.getParent_stock_tx_id());
            entryMap.put("stock_id", entry.getStockId());
            entryMap.put("wallet_tx_id", entry.getWalletTXid());
            entryMap.put("order_status", entry.getOrderStatus().toString());
            entryMap.put("is_buy", entry.is_buy());
            entryMap.put("order_type", entry.getOrderType().toString());
            entryMap.put("stock_price", entry.getPrice());
            entryMap.put("quantity", entry.getQuantity());
            entryMap.put("time_stamp", entry.getTimestamp());

            data.add(entryMap);
        }

        return data;
    }


}