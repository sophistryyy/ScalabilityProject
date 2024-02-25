package seng468.scalability.matchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.repositories.WalletRepository;

import java.util.*;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository){
        this.matchingEngineOrdersRepository = repository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;

    }

    public String placeOrder(PlaceStockOrderRequest req, String username)
    /*
    * If return type is String, error happened, otherwise null means success
    */ {
        String message = basicVerifier(req);
        if(message != null){return message;}

        StockOrder.OrderType orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        StockOrder order = new StockOrder(req.getStock_id(), req.getIs_buy(), orderType, req.getQuantity(), req.getPrice(), username);
        matchingEngineOrdersRepository.save(order);

        int req_stock_id = order.getStockId();
        LinkedList<StockOrder> lstOfSellStocks = matchingEngineOrdersRepository.getAllSellByStock_id(req_stock_id);//at least 1 element in it and no completed transactions
        LinkedList<StockOrder> lstOfBuyStocks = matchingEngineOrdersRepository.getAllBuyByStock_id(req_stock_id);
        OrderBook orderBook = new OrderBook(lstOfSellStocks, lstOfBuyStocks);

        try {//just in case
            try_matching(orderBook);
        }catch(Exception e)
        {
            return e.getMessage();
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
    public void try_matching(OrderBook orderBook)
            /*
            * Assumed: if limit order MONEY IS ALREADY DEDUCTED AND USER HAS ENOUGH
            * case: user has market order but no money
            * */

    {
        StockOrder buyOrder =  orderBook.getBuyHead();
        StockOrder sellOrder = orderBook.getSellHead();

        if(buyOrder == null || sellOrder == null){return;}

        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET){
            if(!orderBook.isMatchPossible()){return;} //buyer's price is guaranteed to be higher

            int sellingPrice = sellOrder.getPrice();
            int sellingStocks = sellOrder.getTrueRemainingQuantity();
            int buyingPrice = buyOrder.getPrice();
            int buyingStocks = buyOrder.getTrueRemainingQuantity();

            if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
            {
                //SELL is completed
                if(sellOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction
                {
                    StockOrder completedSellStockOrder = sellOrder.createCopy(sellingStocks, StockOrder.OrderStatus.COMPLETED);
                    matchingEngineOrdersRepository.save(completedSellStockOrder);
                }
                sellOrder.setTrueRemainingQuantity(0);
                sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed
                Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());
                sellerWallet.incrementBalance(sellingPrice * sellingStocks);//add money to seller based on trueRemaining quantity
                //create wallet transaction

                //add SELLER PORTFOLIO

                orderBook.popSellOrder();

                //BUY is partially completed
                buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
                buyOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
                //add stock to portfolio (REFACTOR)
                PortfolioEntry buyerPortfolioByStockId  = portfolioRepository.findEntryByStockIdAndUsername(buyOrder.getStockId(), buyOrder.getUsername()); //add stocks to buyer's portfolio
                if (buyerPortfolioByStockId == null) {
                    buyerPortfolioByStockId = new PortfolioEntry(buyOrder.getStockId(), buyOrder.getUsername(), sellingStocks);
                } else {
                    buyerPortfolioByStockId.addQuantity(sellingStocks);
                }
                portfolioRepository.save(buyerPortfolioByStockId);

                //create wallet transaction for buyer

                //add completed transaction to buy
                StockOrder completedBuyerStockOrder = buyOrder.createCopy(sellingStocks, StockOrder.OrderStatus.COMPLETED);
                completedBuyerStockOrder.setPrice(sellingPrice);//how much the stock was bought for
                matchingEngineOrdersRepository.save(completedBuyerStockOrder);
            }
            else if(buyingStocks < sellingStocks)//seller has more
            {
                //SELL is partially completed
                sellOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
                sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
                Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());
                sellerWallet.incrementBalance(sellingPrice * buyingStocks);//add money to seller based on trueRemaining quantity
                //add wallet transaction

                //add SELLER PORTFOLIO

                //add completed order transaction for seller
                StockOrder completedSellerStockOrder = sellOrder.createCopy(buyingStocks, StockOrder.OrderStatus.COMPLETED);
                matchingEngineOrdersRepository.save(completedSellerStockOrder);

                //BUYER is complete
                if(buyOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction since it was partially fulfilled before
                {
                    StockOrder completedBuyerStockOrder = buyOrder.createCopy(buyingStocks, StockOrder.OrderStatus.COMPLETED);
                    matchingEngineOrdersRepository.save(completedBuyerStockOrder);
                }
                buyOrder.setTrueRemainingQuantity(0);
                buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed (original parent!)
                //create wallet transaction

                //add stock to portfolio (REFACTOR)
                PortfolioEntry buyerPortfolioByStockId  = portfolioRepository.findEntryByStockIdAndUsername(buyOrder.getStockId(), buyOrder.getUsername()); //add stocks to buyer's portfolio
                if (buyerPortfolioByStockId == null) {
                    buyerPortfolioByStockId = new PortfolioEntry(buyOrder.getStockId(), buyOrder.getUsername(), buyingStocks);
                } else {
                    buyerPortfolioByStockId.addQuantity(buyingStocks);
                }
                portfolioRepository.save(buyerPortfolioByStockId);


                orderBook.popBuyOrder();
            }
            else{//seller stocks = buyer stocks quantity, no more to match
                if(sellOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction
                {
                    StockOrder completedSellStockOrder = sellOrder.createCopy(sellingStocks, StockOrder.OrderStatus.COMPLETED);
                    matchingEngineOrdersRepository.save(completedSellStockOrder);
                }
                //SELLER PORTFOLIO
                sellOrder.setTrueRemainingQuantity(0);
                sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //create wallet transaction

                orderBook.popSellOrder();

                if(buyOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction since it was partially fulfilled before
                {
                    StockOrder completedBuyerStockOrder = buyOrder.createCopy(buyingStocks, StockOrder.OrderStatus.COMPLETED);
                    matchingEngineOrdersRepository.save(completedBuyerStockOrder);
                }
                buyOrder.setTrueRemainingQuantity(0);
                buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //REFACTOR
                PortfolioEntry buyerPortfolioByStockId  = portfolioRepository.findEntryByStockIdAndUsername(buyOrder.getStockId(), buyOrder.getUsername()); //add stocks to buyer's portfolio
                if (buyerPortfolioByStockId == null) {
                    buyerPortfolioByStockId = new PortfolioEntry(buyOrder.getStockId(), buyOrder.getUsername(), buyingStocks);
                } else {
                    buyerPortfolioByStockId.addQuantity(buyingStocks);
                }
                portfolioRepository.save(buyerPortfolioByStockId);
                //create wallet transaction

                orderBook.popBuyOrder();
            }
            try_matching(orderBook);
        }
    }
}
