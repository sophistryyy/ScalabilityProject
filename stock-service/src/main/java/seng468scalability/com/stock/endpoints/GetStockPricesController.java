package seng468scalability.com.stock.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.entity.StockPrices;
import seng468scalability.com.stock.repositories.StockRepository;
import seng468scalability.com.stock.util.GetStockPricesService;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
public class GetStockPricesController {

    private final GetStockPricesService pricesService;
    private final StockRepository stockRepository;

    @GetMapping(path="/getStockPrices")
    public Response getStockPrices( ) {
        try{
            List<Stock> stocks = stockRepository.findAll();
            List<StockPrices> entries = new LinkedList<>();
            for(Stock curStock: stocks){
                Long price;

                System.out.println("---------------!");
                price = pricesService.getStockPriceWithCache(curStock.getId());

                System.out.println("Price returned: " + price);
                if(price == null){//not in cache
                    System.out.println("used search! 1");
                    price = pricesService.searchCompletedStockTransactions(curStock.getId());
                }

                if(price == null){
                    System.out.println("used search! 2");
                    price = pricesService.searchInProgressStockTransactions(curStock.getId());
                }
                if(price != null) {
                    StockPrices stockPrice = new StockPrices(curStock.getName(), curStock.getId(), price);
                    entries.add(stockPrice);
                }
                System.out.println("---------------!");
            }
            return Response.ok(formatData(entries));
        }catch (Exception e){
            log.info("getStockPrices error. " +e.getMessage());
            return Response.error("Error with getting stock prices.");
        }
    }

    @PostMapping(path="/internal/updateStockPrices")
    public void updateCacheValues(@RequestBody StockPrices entry){
        if(entry != null && entry.getPrice() != null && entry.getStockId() != null){
            System.out.println("Adding value to cache (2) "  + entry.toString());
            pricesService.updateStockPrice(entry.getStockId(), entry.getPrice());
        }
    }

    private List<Map<String, Object>> formatData(List<StockPrices> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (StockPrices entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_id", entry.getStockId());
            entryMap.put("stock_name", entry.getStockName());
            entryMap.put("current_price", entry.getPrice());
            data.add(entryMap);
        }
        return data;
    }
}
