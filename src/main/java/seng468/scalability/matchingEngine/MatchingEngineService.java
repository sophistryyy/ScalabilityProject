package seng468.scalability.matchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.wallet.Wallet;
import seng468.scalability.wallet.WalletRepository;

import java.util.*;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository){
        this.matchingEngineOrdersRepository = repository;
        this.stockRepository = stockRepository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;

    }

    public String placeOrder(StockOrder req)
    /*
    * If return type is String, error happened, otherwise null means success
    */
    {
        StockOrder order = new StockOrder(req.getStockId(), req.getIs_buy(), req.getOrderType(), req.getQuantity(), req.getPrice());
        if (order.getOrderType() == StockOrder.OrderType.MARKET &&  order.getPrice() != null)
        {
            return "Market orders can't have price";
        }
        matchingEngineOrdersRepository.save(order);
        int req_stock_id = order.getStockId();

        LinkedList<StockOrder> lstOfSellStocks = matchingEngineOrdersRepository.getAllSellByStock_id(req_stock_id);//at least 1 element in it and no completed transactions
        LinkedList<StockOrder> lstOfBuyStocks =  matchingEngineOrdersRepository.getAllBuyByStock_id(req_stock_id);
        OrderBook orderBook = new OrderBook(matchingEngineOrdersRepository ,lstOfSellStocks, lstOfBuyStocks);


        return orderBook.toString();
    }
}
