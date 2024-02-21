package seng468.scalability.matchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.repositories.StockRepository;

import java.util.List;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final StockRepository stockRepository;
    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository){
        this.matchingEngineOrdersRepository = repository;
        this.stockRepository = stockRepository;
    }

    public StockOrder placeOrder(StockOrder req)
    {
        StockOrder order = new StockOrder(req.getStockId(), req.getIs_buy(), req.getOrderType(), req.getQuantity(), req.getPrice());
        matchingEngineOrdersRepository.save(order);
        Integer transactionId = order.getTransaction_id();

        return order;
    }
}
