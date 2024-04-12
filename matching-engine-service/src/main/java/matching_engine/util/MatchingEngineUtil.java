package matching_engine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching_engine.entity.OrderBook;
import matching_engine.entity.StockTransaction;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@RequiredArgsConstructor
@Slf4j
@Service
public class MatchingEngineUtil {


    private final OrderBook orderBook;


    public void insertNewElement(StockTransaction transaction){
        try {
            orderBook.addStockTransaction(transaction);
            System.out.println(orderBook);
        }catch (Exception e){
            log.info("Error add stock to orderBook. " + e.getMessage());
        }
    }
}
