package seng468.scalability.matchingEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

@Component
public class MatchingEngineUtil {
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;
    private final StockRepository stockRepository;
    private final WalletTXRepository walletTXRepository;

    @Autowired
    public MatchingEngineUtil(MatchingEngineOrdersRepository repository, StockRepository stockRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository, WalletTXRepository walletTXRepository){
        this.matchingEngineOrdersRepository = repository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;
        this.stockRepository = stockRepository;
        this.walletTXRepository = walletTXRepository;

    }
    public Boolean isOrderMarketOne(StockOrder order)
    {
        return order.getOrderType() == StockOrder.OrderType.MARKET;
    }

    public void returnMoney(Integer stockTXid,String username, Integer balance){
        //remove wallet transaction
        walletTXRepository.deleteByStockTXId(stockTXid);
        //return money
        Wallet wallet = walletRepository.findByUsername(username);
        wallet.incrementBalance(balance);
    }

    public void saveToPortfolio(StockOrder buyOrder, Integer buyingStocks)
    {
        PortfolioEntry buyerPortfolioByStockId  = portfolioRepository.findEntryByStockIdAndUsername(buyOrder.getStockId(), buyOrder.getUsername()); //add stocks to buyer's portfolio
        int stock_id = buyOrder.getStockId();
        if (buyerPortfolioByStockId == null) {
            buyerPortfolioByStockId = new PortfolioEntry(stock_id,stockRepository.findStockNameById(stock_id) ,buyOrder.getUsername(), buyingStocks);
        } else {
            buyerPortfolioByStockId.addQuantity(buyingStocks);
        }
        portfolioRepository.save(buyerPortfolioByStockId);
    }

    public void createStockTransaction(StockOrder order, Integer remainingStocks, Integer priceBoughtFor){
        if(order.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction
        {
            StockOrder completedSellStockOrder = order.createCopy(remainingStocks, StockOrder.OrderStatus.COMPLETED);
            if(priceBoughtFor > 0){
                completedSellStockOrder.setPrice(priceBoughtFor);
            }
            matchingEngineOrdersRepository.save(completedSellStockOrder);
        }
    }
}
