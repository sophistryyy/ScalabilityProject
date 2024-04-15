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

@Service
public class GetStockPricesService {
    private final Cache stockPriceCache;
    private final StockTransactionsRepository stockTransactionsRepository;

    @Autowired
    public GetStockPricesService(CacheManager cacheManager, StockTransactionsRepository stockTransactionsRepository) {
        this.stockPriceCache = cacheManager.getCache("stockPriceCache");
        this.stockTransactionsRepository = stockTransactionsRepository;
    }

    @CachePut(value = "stockPriceCache", key = "#stockId")
    public Long updateStockPrice(Long stockId, Long newPrice){
        System.out.println(stockPriceCache.getNativeCache());
        System.out.println("Updated cache");
        return newPrice;
    }

    @Cacheable(value = "stockPriceCache", key = "#stockId")
    public Long getStockPriceWithCache(Long stockId){
        System.out.println(stockPriceCache.getNativeCache());
        return null;
    }

    public Long searchCompletedStockTransactions(Long stock_id){
        StockTransaction lastCompletedTransaction = stockTransactionsRepository.findByStockIdAndOrderStatusOrderByTimestampDesc(stock_id, OrderStatus.COMPLETED);
        if (lastCompletedTransaction != null) {
            updateStockPrice(stock_id, lastCompletedTransaction.getPrice()); // Update cache
            return lastCompletedTransaction.getPrice();
        }
        return null;
    }

    public Long searchInProgressStockTransactions(Long stock_id){
        StockTransaction lastCompletedTransaction = stockTransactionsRepository.findByStockIdAndOrderStatusOrderByPriceAsc(stock_id, OrderStatus.IN_PROGRESS);
        if (lastCompletedTransaction != null) {
            updateStockPrice(stock_id, lastCompletedTransaction.getPrice()); // Update cache
            return lastCompletedTransaction.getPrice();
        }
        return null;
    }
}
