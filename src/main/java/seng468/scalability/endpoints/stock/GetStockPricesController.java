package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineUtil;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockPrices;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.WalletRepository;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="getStockPrices")
public class GetStockPricesController {


    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public GetStockPricesController(MatchingEngineUtil matchingEngineUtil){


        this.matchingEngineUtil = matchingEngineUtil;
    }

    @GetMapping
    public Response getStockPricesPost()
    {
        List<StockPrices> lstOfPrices = matchingEngineUtil.getBestPrices();
        List<Map<String, Object>> data = formatData(lstOfPrices);
        return Response.ok(data);
    }
    private List<Map<String, Object>> formatData(List<StockPrices> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (StockPrices entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_id", entry.getStock().getId());
            entryMap.put("stock_name", entry.getStock().getName());
            entryMap.put("current_price", entry.getPrice());
            data.add(entryMap);
        }
        return data;
    }
}
