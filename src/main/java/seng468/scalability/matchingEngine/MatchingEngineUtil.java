package seng468.scalability.matchingEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import seng468.scalability.models.entity.*;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

import java.util.*;

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

    public String verifyIfEnough(StockOrder order)
    {
        int stockId = order.getStockId();
        if(order.getIs_buy()) {//for buyer remove
            PortfolioEntry portfolioEntry = portfolioRepository.findEntryByStockIdAndUsername(stockId, order.getUsername());
            if (portfolioEntry == null) {
                return "User doesn't have the following stock in portfolio: " + stockId;
            }
            String errorMessage = removeQuantityFromPortfolio(portfolioEntry, order.getQuantity());
            if (errorMessage != null) {
                return errorMessage;
            }
        }else{
            Wallet wallet = walletRepository.findByUsername(order.getUsername());
            if(order.getOrderType() == StockOrder.OrderType.LIMIT)
            {
                int toDeduct = order.getPrice() * order.getQuantity();
                try {
                    wallet.decrementBalance(toDeduct);
                }catch(Exception e)
                {
                    return "User doesn't have enough money to cover this stock. Current balance is " + wallet.getBalance();
                }
                //create wallet transaction
                WalletTX walletTX = new WalletTX(order.getStock_tx_id(), true, toDeduct);

                try{
                    walletTXRepository.saveNewWalletTX(walletTX);
                }catch(Exception e)
                {
                    return e.getMessage();
                }
            }
        }

        return null;
    }

    public String basicVerifier(PlaceStockOrderRequest req)
    {
        StockOrder.OrderType orderType;
        try {
            orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        } catch (Exception e) {
            return "Incorrect value of order type";
        }
        if (orderType == StockOrder.OrderType.MARKET && req.getPrice() != null) {
            return "MARKET orders can't have price, set it to null.";
        }
        if (orderType == StockOrder.OrderType.LIMIT) {
            if(req.getPrice() == null && req.getPrice() <= 0) {
                return "LIMIT orders' price has to be more than 0";
            }
        }
        if(req.getQuantity() != null && req.getQuantity() <= 0){
            return "Please set quantity to more than 0";
        }
        return null;
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

    public List<StockPrices> getBestPrices()
    {
        List<StockPrices> stockPriceLst = new ArrayList<>();
        List<Stock> lstOfStocks= stockRepository.findAll();

        for(Stock stock : lstOfStocks)
        {
            LinkedList<StockOrder> lowestSellOrder = matchingEngineOrdersRepository.getLowestSellOrderByStockId(stock.getId(), PageRequest.of(0, 1));
            if(lowestSellOrder.peek() != null) {
                Integer price = lowestSellOrder.peek().getPrice();
                StockPrices stockPrice = new StockPrices(stock, price);
                stockPriceLst.add(stockPrice);
            }
        }
        return stockPriceLst;
    }

    public Integer getBestPriceByStockId(int stock_id)
    {
        LinkedList<StockOrder> lowestSellOrder = matchingEngineOrdersRepository.getLowestSellOrderByStockId(stock_id, PageRequest.of(0, 1));
        if(lowestSellOrder.peek() == null)
        {
            return null;
        }
        return lowestSellOrder.peek().getPrice();
    }

    public String removeQuantityFromPortfolio(PortfolioEntry portfolioEntry, Integer quantity)
    {
        try {
            portfolioEntry.removeQuantity(quantity);
            if(portfolioEntry.getQuantity() == 0) //if quantity is 0 remove it from portfolio
            {
                portfolioRepository.deleteByStockId(portfolioEntry.getStockId());
            }
        }catch(Exception e)
        {
            return e.getMessage();
        }
        return null;
    }


}
