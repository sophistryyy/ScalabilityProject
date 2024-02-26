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
    private final StockRepository stockRepository;

    @Autowired
    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository, MatchingEngineUtil matchingEngineUtil){
        this.matchingEngineOrdersRepository = repository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;
        this.stockRepository = stockRepository;

        this.matchingEngineUtil = matchingEngineUtil;
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
        Integer priceBoughtFor;
        StockOrder buyOrder =  orderBook.getBuyHead();
        StockOrder sellOrder = orderBook.getSellHead();

        if(buyOrder == null || sellOrder == null){return;}

        if(buyOrder.getOrderType() == StockOrder.OrderType.MARKET && sellOrder.getOrderType() == StockOrder.OrderType.MARKET)
        {
            //need to give priorty somehow if both orders are MARKET and price is null
            orderBook.popBuyOrder();
            orderBook.popSellOrder();
            try_matching(orderBook);
            return; //not sure what to do
        }
        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET) { //seller can be
            if (!orderBook.isMatchPossible()) {
                return;
            } //buyer's price is guaranteed to be higher if limit
        }
        int sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice();
        int sellingStocks = sellOrder.getTrueRemainingQuantity();
        //int buyingPrice = buyOrder.getPrice();
        int buyingStocks = buyOrder.getTrueRemainingQuantity();

        if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
        {
            //if market order need to check if any user enough money
            if(matchingEngineUtil.isOrderMarketOne(buyOrder)) {//then sell is LIMIT
                Wallet buyerWallet = walletRepository.findByUsername(buyOrder.getUsername());
                if (buyerWallet.getBalance() < sellingPrice) //user doesn't have enough even to buy 1
                {                //buy order couldn't be matched, so do the rest
                    orderBook.popBuyOrder();
                    try_matching(orderBook);
                    return;
                }
                //buyer has enough money but maybe not enough to buy all stocks!
                Integer userCanBuyQuantity = sellingPrice;
            }
            if(matchingEngineUtil.isOrderMarketOne(sellOrder))
            {

            }
            //SELL is completed
            matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, -1);//create stock transaction
            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed
            sellOrder.setTrueRemainingQuantity(0);
            Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());
            sellerWallet.incrementBalance(sellingPrice * sellingStocks);//add money to seller based on trueRemaining quantity
            //create wallet transaction

            orderBook.popSellOrder();

            //BUY is partially completed
            buyOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            matchingEngineUtil.createStockTransaction(buyOrder, sellingStocks, sellingPrice);// create a transaction with price bought for
            buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
            //add stock to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, sellingStocks);
            //create wallet transaction for buyer

        }
        else if(buyingStocks < sellingStocks)//seller has more
        {
            //SELL is partially completed
            sellOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            matchingEngineUtil.createStockTransaction(sellOrder, buyingStocks, -1);
            sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
            Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());
            sellerWallet.incrementBalance(sellingPrice * buyingStocks);//add money to seller based on trueRemaining quantity
            //add wallet transaction


            //BUYER is complete
            if(buyOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) //create child transaction since it was partially fulfilled before
            {
                StockOrder completedBuyerStockOrder = buyOrder.createCopy(buyingStocks, StockOrder.OrderStatus.COMPLETED);
                matchingEngineOrdersRepository.save(completedBuyerStockOrder);
            }
            matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice);
            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed (original parent!)
            buyOrder.setTrueRemainingQuantity(0);
            //add stock to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            //create wallet transaction

            orderBook.popBuyOrder();
        }
        else{//seller stocks = buyer stocks quantity, no more to match
            matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, -1);
            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0);
            //create wallet transaction

            orderBook.popSellOrder();

            matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice);
            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0);
            //save to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            //create wallet transaction

            orderBook.popBuyOrder();
        }
        try_matching(orderBook);

    }


}
