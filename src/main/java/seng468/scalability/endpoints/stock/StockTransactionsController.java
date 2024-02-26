package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.models.entity.StockPrices;
import seng468.scalability.models.response.Response;
import seng468.scalability.models.entity.StockOrder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "getStockTransactions")
public class StockTransactionsController {
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    @Autowired
    public StockTransactionsController(MatchingEngineOrdersRepository matchingEngineOrdersRepository)
    {
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
    }
    @GetMapping
    public Response getStockTransactions()
    {
        try{
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            List<StockOrder> transLst = matchingEngineOrdersRepository.findAllByUsername(username);
            List<Map<String, Object>> data = formatData(transLst);
            return Response.ok(data);
        }catch(Exception e)
        {
            return Response.error(e.getMessage());
        }
    }

    private List<Map<String, Object>> formatData(List<StockOrder> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (StockOrder entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_tx_id", entry.getStock_tx_id());
            entryMap.put("parent_tx_id", entry.getParent_stock_tx_id());
            entryMap.put("stock_id", entry.getStockId());
            entryMap.put("wallet_tx_id", entry.getWalletTXid());
            entryMap.put("order_status", entry.getOrderStatus().toString());
            entryMap.put("is_buy", entry.getIs_buy());
            entryMap.put("order_type", entry.getOrderType().toString());
            entryMap.put("stock_price", entry.getPrice());
            entryMap.put("quantity", entry.getQuantity());
            entryMap.put("timestamp", entry.getTimestamp());

            data.add(entryMap);
        }

        return data;
    }
}
