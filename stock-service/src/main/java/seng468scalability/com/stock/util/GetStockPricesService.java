package seng468scalability.com.stock.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;

import java.util.List;

@Service
public class GetStockPricesService {
    private final Cache stockPriceCache;
    private final StockTransactionsRepository stockTransactionsRepository;

    @Autowired
    public GetStockPricesService(CacheManager cacheManager, StockTransactionsRepository stockTransactionsRepository) {
        this.stockPriceCache = cacheManager.getCache("stockPriceCache");
        this.stockTransactionsRepository = stockTransactionsRepository;
    }

    @CachePut(value = "stockPriceCache", key="#p0", condition="#p0!=null")
    public Long updateStockPrice(Long stockId, Long newPrice){
        stockPriceCache.put(stockId, newPrice);
        System.out.println("Updated cache");
        System.out.println("native cache after updating " + stockPriceCache.getNativeCache());
        return newPrice;
    }

    @Cacheable(value = "stockPriceCache", key="#p0", condition="#p0!=null")
    public Long getStockPriceWithCache(Long stockId){
        return null;
    }

    public Long searchCompletedStockTransactions(Long stock_id){
        List<StockTransaction> lastCompletedTransactions = stockTransactionsRepository.findByStockIdAndOrderStatusOrderByTimestampDesc
                (stock_id, OrderStatus.COMPLETED);
        System.out.println("!!!!!!!!!!!!!!");
        System.out.println(lastCompletedTransactions);
        System.out.println("!!!!!!!!!!!!!!");
        if (lastCompletedTransactions == null || lastCompletedTransactions.isEmpty()) {
            return null;
        }
        for(StockTransaction stockTransaction : lastCompletedTransactions){
            if(stockTransaction != null && stockTransaction.getPrice() != null){
                updateStockPrice(stock_id, lastCompletedTransactions.getFirst().getPrice()); // Update cache
                return lastCompletedTransactions.getFirst().getPrice();
            }
        }
        return null;
    }

    public Long searchInProgressStockTransactions(Long stock_id){
        List<StockTransaction> lastCompletedTransactions = stockTransactionsRepository.findByStockIdAndOrderStatusOrderByPriceAsc
                (stock_id, OrderStatus.IN_PROGRESS);
        System.out.println("!!!!!!!!!!!!!!");
        System.out.println(lastCompletedTransactions);
        System.out.println("!!!!!!!!!!!!!!");
        if (lastCompletedTransactions == null || lastCompletedTransactions.isEmpty()) {
            return null;
        }
        for(StockTransaction stockTransaction : lastCompletedTransactions){
            if(stockTransaction != null && stockTransaction.getPrice() != null){
                updateStockPrice(stock_id, lastCompletedTransactions.getFirst().getPrice()); // Update cache
                return lastCompletedTransactions.getFirst().getPrice();
            }
        }
        return null;
    }
}
