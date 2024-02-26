package seng468.scalability.matchingEngine;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seng468.scalability.matchingEngine.specialReturnClass.IntOrError;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.entity.WalletTX;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

import java.util.*;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;
    private final StockRepository stockRepository;
    private final WalletTXRepository walletTXRepository;

    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository,WalletTXRepository walletTXRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository, MatchingEngineUtil matchingEngineUtil){
        this.matchingEngineOrdersRepository = repository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;
        this.stockRepository = stockRepository;
        this.walletTXRepository = walletTXRepository;
        this.matchingEngineUtil = matchingEngineUtil;
    }

    @Transactional
    public String placeOrder(PlaceStockOrderRequest req, String username)
    /*
    * If return type is String, error happened, otherwise null means success
    */ {
        String message = matchingEngineUtil.basicVerifier(req);
        if(message != null){return message;}

        StockOrder.OrderType orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        StockOrder order = new StockOrder(req.getStock_id(), req.getIs_buy(), orderType, req.getQuantity(), req.getPrice(), username);
        IntOrError verifier = matchingEngineUtil.verifyIfEnough(order);
        if(verifier.getMessage() != null){return verifier.getMessage();}
        order.setStock_tx_id(verifier.getWalletTXid());
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


    @Transactional
    public void try_matching(OrderBook orderBook)
        /*
            * Assumed: if limit order MONEY IS ALREADY DEDUCTED AND USER HAS ENOUGH
            * MARKET order: money was not deducted!
            * case: user has market order but no money
            * */

    {

        StockOrder buyOrder =  orderBook.getBuyHead();
        StockOrder sellOrder = orderBook.getSellHead();

        if(buyOrder == null || sellOrder == null){return;}

        if(buyOrder.getOrderType() == StockOrder.OrderType.MARKET && sellOrder.getOrderType() == StockOrder.OrderType.MARKET)
        {
            //need to give priorty somehow if both orders are MARKET and price is null
            orderBook.popBuyOrder();
            orderBook.popSellOrder();
            try_matching(orderBook);
            return; //not sure what to do, unhandked case
        }
        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET) {
            if (!orderBook.isMatchPossible()) {//match isn't possible at all
                return;
            } //buyer's price is guaranteed to be higher if limit
        }
        int sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice(); //handle Market orders
        int sellingStocks = sellOrder.getTrueRemainingQuantity();
        //int buyingPrice = buyOrder.getPrice();
        int buyingStocks = buyOrder.getTrueRemainingQuantity();

        Boolean isBuyOrderMarketOne = matchingEngineUtil.isOrderMarketOne(buyOrder);
        Wallet buyerWallet = walletRepository.findByUsername(buyOrder.getUsername());
        Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());
        //if market order need to check if any user enough money
        if(isBuyOrderMarketOne) {//then sell is LIMIT

            if (buyerWallet.getBalance() < sellingPrice) //user doesn't have enough even to buy 1
            {                //buy order couldn't be matched, so do the rest
                orderBook.popBuyOrder();
                try_matching(orderBook);
                return;
            }
            //buyer has enough money but maybe not enough to buy all stocks!
            int buyerCanAffordQuantity = (int) Math.floor((double) buyerWallet.getBalance() / sellingPrice);
            if(buyerCanAffordQuantity < buyingStocks)
            {
                buyingStocks = buyerCanAffordQuantity;
            }
            int toDeduct = sellingPrice * buyingStocks;
            buyerWallet.decrementBalance(toDeduct); //might throw expcetion

            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(), buyOrder.getStock_tx_id(), true, toDeduct);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);

        }


        if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
        {
            //SELL is completed
            //create wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrder.getStock_tx_id(), false, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);
            //update status
            matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, sellingPrice, sellerWalletTX.getWalletTXId());//create stock transaction
            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed
            sellOrder.setTrueRemainingQuantity(0);
            sellerWallet.incrementBalance(sellingPrice * sellingStocks);//add money to seller based on trueRemaining quantity
            orderBook.popSellOrder();

            //BUY is partially completed
            //create wallet transaction for buyer
            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrder.getStock_tx_id(), true, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);
            //update status
            buyOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            matchingEngineUtil.createStockTransaction(buyOrder, sellingStocks, sellingPrice, buyerWalletTX.getWalletTXId());// create a transaction with price bought for
            buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
            //add stock to portfolio
            if(buyOrder.getPrice() == null)
            {
                buyOrder.setPrice(sellingPrice);
            }
            matchingEngineUtil.saveToPortfolio(buyOrder, sellingStocks);
        }
        else if(buyingStocks < sellingStocks)//seller has more
        {
            //SELL is partially completed
            //add wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrder.getStock_tx_id(), false, sellingPrice*buyingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);
            //update status
            sellOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            matchingEngineUtil.createStockTransaction(sellOrder, buyingStocks, sellingPrice, sellerWalletTX.getWalletTXId());//might have to change this number
            sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
            sellerWallet.incrementBalance(sellingPrice * buyingStocks);//add money to seller based on trueRemaining quantity

            //BUYER is complete
            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrder.getStock_tx_id(), true, sellingPrice*buyingStocks);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);
            //update status
            matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice, buyerWalletTX.getWalletTXId());
            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed (original parent!)
            buyOrder.setTrueRemainingQuantity(0);
            //add stock to portfolio
            if(buyOrder.getPrice() == null)
            {
               buyOrder.setPrice(sellingPrice);
            }
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            //create wallet transaction
            orderBook.popBuyOrder();
        }
        else{//seller stocks = buyer stocks quantity, no more to match
            //create wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrder.getStock_tx_id(), false, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);
            //update status
            matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, sellingPrice, sellerWalletTX.getWalletTXId());
            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0);
            orderBook.popSellOrder();

            //create wallet transaction
            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrder.getStock_tx_id(), true, sellingPrice*buyingStocks);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);
            //update status
            matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice, buyerWalletTX.getWalletTXId());
            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0);
            //save to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            if(buyOrder.getPrice() == null)
            {
                buyOrder.setPrice(sellingPrice);
            }
            orderBook.popBuyOrder();
        }
        try_matching(orderBook);

    }


}
