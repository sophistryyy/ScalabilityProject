package seng468.scalability.matchingEngine;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import seng468.scalability.matchingEngine.specialReturnClass.IntOrError;
import seng468.scalability.models.entity.*;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

import java.util.*;
import java.util.stream.Collectors;

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
        if(req.getQuantity() == null || req.getQuantity() <= 0){
            return "Please set quantity to more than 0";
        }
        return null;
    }

    public IntOrError verifyIfEnough(StockOrder order)
    {
        IntOrError completedOrError = new IntOrError();//class that has message and WalletTXid. if message then error happened
        Long stockId = order.getStockId();
        if (!stockRepository.existsById(stockId))
        {
            completedOrError.setMessage("Invalid stock Id. It doesn't exist");
            return completedOrError;
        }
        if(!order.getIs_buy()) {//for seller
            PortfolioEntry portfolioEntry = portfolioRepository.findEntryByStockIdAndUsername(stockId, order.getUsername());
            if (portfolioEntry == null) {
                matchingEngineOrdersRepository.delete(order);
                completedOrError.setMessage("User doesn't have the following stock id in portfolio: " + stockId);
                return completedOrError;
            }
            String errorMessage = removeQuantityFromPortfolio(portfolioEntry, order.getQuantity());
            if (errorMessage != null) {
                matchingEngineOrdersRepository.delete(order);
                completedOrError.setMessage(errorMessage);
                return completedOrError;
            }
        }else{
            Wallet wallet = walletRepository.findByUsername(order.getUsername());
            if(order.getOrderType() == StockOrder.OrderType.LIMIT)
            {
                Long toDeduct = order.getPrice() * order.getQuantity();
                try {
                    wallet.decrementBalance(toDeduct);
                }catch(Exception e)
                {
                    matchingEngineOrdersRepository.delete(order);
                    completedOrError.setMessage("User doesn't have enough money to cover this stock. Current balance is " + wallet.getBalance());
                    return completedOrError;
                }
                //create wallet transaction for LIMIT buy orders
                WalletTX walletTX = new WalletTX(order.getUsername(),order.getStock_tx_id(), true, toDeduct);
                try{
                    walletTXRepository.saveNewWalletTX(walletTX);
                    completedOrError.setWalletTXid(walletTX.getWalletTXId());
                }catch(Exception e)
                {
                    matchingEngineOrdersRepository.delete(order);
                    completedOrError.setMessage(e.getMessage());
                    return completedOrError;
                }
            }
        }

        return completedOrError;
    }

    public String removeQuantityFromPortfolio(PortfolioEntry portfolioEntry, Long quantity)
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


    public Boolean isOrderMarketOne(StockOrder order)
    {
        return order.getOrderType() == StockOrder.OrderType.MARKET;
    }

    public void returnMoney(Long stockTXid, String username, Long balance) throws Exception{
        //must be used in @Transactional
        //remove wallet transaction
        walletTXRepository.deleteByStockTXId(stockTXid);
        //return money
        Wallet wallet = walletRepository.findByUsername(username);
        wallet.incrementBalance(balance);
    }


    public void saveToPortfolio(StockOrder buyOrder, Long buyingStocks)
    {
        PortfolioEntry buyerPortfolioByStockId  = portfolioRepository.findEntryByStockIdAndUsername(buyOrder.getStockId(), buyOrder.getUsername()); //add stocks to buyer's portfolio
        Long stock_id = buyOrder.getStockId();
        if (buyerPortfolioByStockId == null) {
            buyerPortfolioByStockId = new PortfolioEntry(stock_id, stockRepository.findStockNameById(stock_id) ,buyOrder.getUsername(), buyingStocks);
        } else {
            buyerPortfolioByStockId.addQuantity(buyingStocks);
        }
        portfolioRepository.save(buyerPortfolioByStockId);
    }


    public Long createStockTransaction(StockOrder order, Long remainingStocks,
                                       Long priceBoughtFor, Long walletTXid, StockOrder.OrderType orderType){

        StockOrder completedStockOrder = order.createCopy(remainingStocks, StockOrder.OrderStatus.COMPLETED);
        if(priceBoughtFor > 0){
            completedStockOrder.setPrice(priceBoughtFor);
        }
        completedStockOrder.setWalletTXid(walletTXid);
        completedStockOrder.setOrderType(orderType);
        matchingEngineOrdersRepository.save(completedStockOrder);
        return completedStockOrder.getStock_tx_id();
    }

    //Rework this!
    public List<StockPrices> getBestPrices() {
        return stockRepository.findAll().stream()
                .map(stock -> {
                    StockOrder lowestSellOrder = matchingEngineOrdersRepository.getLastSellOrderByStockId(stock.getId(), PageRequest.of(0, 1)).peekFirst();

                    if (lowestSellOrder == null) {
                        lowestSellOrder = matchingEngineOrdersRepository.getLowestSellOrderByStockId(stock.getId(), PageRequest.of(0, 1)).peekFirst();
                    }

                    return lowestSellOrder != null
                            ? new StockPrices(stock, lowestSellOrder.getPrice())
                            : null;
                })
                .filter(stockPrice -> stockPrice != null)
                .collect(Collectors.toList());
    }

    /*
    public Integer getBestPriceByStockId(int stock_id)
    {
        LinkedList<StockOrder> lowestSellOrder = matchingEngineOrdersRepository.getLowestSellOrderByStockId(stock_id, PageRequest.of(0, 1));
        if(lowestSellOrder.peek() == null)
        {
            return null;
        }
        return lowestSellOrder.peek().getPrice();
    }
    */


    //use with @Transactional
    @Transactional
    public void removeStockTransaction(StockOrder order)
    {
        matchingEngineOrdersRepository.delete(order);
    }



}
